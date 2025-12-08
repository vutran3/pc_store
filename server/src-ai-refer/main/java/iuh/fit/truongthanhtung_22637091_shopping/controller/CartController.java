package iuh.fit.truongthanhtung_22637091_shopping.controller;

import iuh.fit.truongthanhtung_22637091_shopping.entity.Cart;
import iuh.fit.truongthanhtung_22637091_shopping.entity.Customer;
import iuh.fit.truongthanhtung_22637091_shopping.entity.Order;
import iuh.fit.truongthanhtung_22637091_shopping.entity.OrderLine;
import iuh.fit.truongthanhtung_22637091_shopping.service.CartService;
import iuh.fit.truongthanhtung_22637091_shopping.service.CustomerService;
import iuh.fit.truongthanhtung_22637091_shopping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')") // Chỉ CUSTOMER và ADMIN có thể sử dụng giỏ hàng
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    /**
     * Xem giỏ hàng
     */
    @GetMapping
    public String viewCart(Authentication auth, Model model) {
        String username = auth.getName();
        Cart cart = cartService.getCartByUsername(username);
        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("totalAmount", cart.getTotalAmount());
        model.addAttribute("totalItems", cart.getTotalItems());
        return "cart/view";
    }

    /**
     * Thêm sản phẩm vào giỏ
     */
    @PostMapping("/add")
    public String addToCart(Authentication auth,
                            @RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes) {
        try {
            String username = auth.getName();
            cartService.addToCart(username, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/product";
    }

    /**
     * Cập nhật số lượng sản phẩm
     */
    @PostMapping("/update")
    public String updateCart(Authentication auth,
                             @RequestParam Long productId,
                             @RequestParam int quantity,
                             RedirectAttributes redirectAttributes) {
        try {
            String username = auth.getName();
            cartService.updateCartItem(username, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    /**
     * Xóa sản phẩm khỏi giỏ
     */
    @GetMapping("/remove/{productId}")
    public String removeFromCart(Authentication auth,
                                 @PathVariable Long productId,
                                 RedirectAttributes redirectAttributes) {
        try {
            String username = auth.getName();
            cartService.removeFromCart(username, productId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    @GetMapping("/clear")
    public String clearCart(Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            String username = auth.getName();
            cartService.clearCart(username);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa toàn bộ giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    /**
     * Hiển thị form checkout - CUSTOMER có thể đặt hàng
     */
    @GetMapping("/checkout")
    public String showCheckoutForm(Authentication auth, Model model) {
        String username = auth.getName();
        Cart cart = cartService.getCartByUsername(username);

        if (cart.getItems().isEmpty()) {
            model.addAttribute("errorMessage", "Giỏ hàng trống!");
            return "redirect:/cart";
        }

        model.addAttribute("cart", cart);

        // Nếu là ADMIN thì cho chọn customer
        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            model.addAttribute("customers", customerService.findAll());
        } else {
            // Nếu là CUSTOMER thì tự động lấy thông tin customer
            Long customerId = getCustomerIdFromUsername(username);
            if (customerId != null) {
                Customer customer = customerService.findById(customerId);
                model.addAttribute("customerId", customerId);
                model.addAttribute("customerInfo", customer);
            }
        }

        return "cart/checkout";
    }

    /**
     * Tạo đơn hàng từ giỏ hàng - CUSTOMER lưu Order
     */
    @PostMapping("/checkout")
    public String checkout(Authentication auth,
                           @RequestParam Long customerId,
                           RedirectAttributes redirectAttributes) {
        try {
            String username = auth.getName();
            Cart cart = cartService.getCartByUsername(username);

            if (cart.getItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng trống!");
                return "redirect:/cart";
            }

            // Kiểm tra quyền: CUSTOMER chỉ được đặt hàng cho chính mình
            boolean isCustomer = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));

            if (isCustomer) {
                Long userCustomerId = getCustomerIdFromUsername(username);
                if (!customerId.equals(userCustomerId)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Bạn chỉ được đặt hàng cho chính mình!");
                    return "redirect:/cart/checkout";
                }
            }

            // Tạo order từ cart
            Customer customer = customerService.findById(customerId);
            Order order = new Order(customer); // Status tự động = "PENDING"

            // Thêm order lines từ cart items
            for (var cartItem : cart.getItems()) {
                OrderLine orderLine = new OrderLine(order, cartItem.getProduct(), cartItem.getQuantity());
                order.addOrderLine(orderLine);
            }

            // Tính tổng tiền
            order.calculateTotalAmount();

            // Lưu order
            order = orderService.save(order);

            // Xóa giỏ hàng sau khi đặt hàng thành công
            cartService.clearCart(username);

//            Cap nhat so luong san pham sau khi dat hang
            for (var orderLine : order.getOrderLines()) {
                cartService.updateProductStock(orderLine.getProduct().getId(), orderLine.getQuantity());
            }

            // Thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đặt hàng thành công! Mã đơn hàng: #" + order.getId() +
                            ". Đơn hàng đang ở trạng thái chờ xác nhận.");

            // Phân biệt redirect theo role
            if (isCustomer) {
                // CUSTOMER: Chuyển về trang "Đơn hàng của tôi"
                return "redirect:/order/my-orders";
            } else {
                // ADMIN: Chuyển đến trang chi tiết đơn hàng để chỉnh sửa
                return "redirect:/order/detail/" + order.getId();
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi đặt hàng: " + e.getMessage());
            return "redirect:/cart/checkout";
        }
    }

    // Helper method để map username -> customerId
    private Long getCustomerIdFromUsername(String username) {
        // Mapping tạm thời
        return switch (username) {
            case "customer1" -> 1L;
            case "customer2" -> 2L;
            case "admin" -> 1L;
            default -> null;
        };
    }
}
