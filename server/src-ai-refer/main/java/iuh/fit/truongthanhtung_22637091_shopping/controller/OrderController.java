package iuh.fit.truongthanhtung_22637091_shopping.controller;

import iuh.fit.truongthanhtung_22637091_shopping.entity.Customer;
import iuh.fit.truongthanhtung_22637091_shopping.entity.Order;
import iuh.fit.truongthanhtung_22637091_shopping.service.CustomerService;
import iuh.fit.truongthanhtung_22637091_shopping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    // List all orders - ADMIN xem tất cả
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderService.findAll();
        model.addAttribute("orders", orders);
        return "order/list";
    }

    // CUSTOMER xem đơn hàng của mình
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/my-orders")
    public String myOrders(Authentication authentication, Model model) {
        // Lấy username từ authentication
        String username = authentication.getName();

        // TODO: Cần liên kết User với Customer để lấy được customerId
        // Tạm thời giả sử customer1 -> customerId = 1, customer2 -> customerId = 2
        Long customerId = getCustomerIdFromUsername(username);

        if (customerId != null) {
            List<Order> orders = orderService.findByCustomerId(customerId);
            model.addAttribute("orders", orders);
            model.addAttribute("isMyOrders", true);
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy thông tin khách hàng");
        }

        return "order/list";
    }

    // Search orders - CUSTOMER và ADMIN
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/search")
    public String searchOrders(@RequestParam(value = "customerId", required = false) Long customerId,
                               @RequestParam(value = "status", required = false) String status,
                               Model model) {
        List<Order> orders;
        if (customerId != null) {
            orders = orderService.findByCustomerId(customerId);
        } else if (status != null && !status.isEmpty()) {
            orders = orderService.findByStatus(status);
        } else {
            orders = orderService.findAll();
        }
        model.addAttribute("orders", orders);
        return "order/list";
    }

    // Show order detail - CUSTOMER và ADMIN
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/detail/{id}")
    public String showOrder(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        model.addAttribute("orderLines", order.getOrderLines());
        return "order/detail";
    }

    // Show create form - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("customers", customerService.findAll());
        return "order/form";
    }

    // Create order - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createOrder(@RequestParam Long customerId) {
        Customer customer = customerService.findById(customerId);
        Order order = new Order(customer);
        order = orderService.save(order);
        return "redirect:/order/detail/" + order.getId();
    }

    // Update order status - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateStatus/{id}")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateStatus(id, status);
        return "redirect:/order/detail/" + id;
    }

    // Delete order - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        return "redirect:/order";
    }

    // Helper method để map username -> customerId
    // TODO: Nên tạo quan hệ User - Customer trong database
    private Long getCustomerIdFromUsername(String username) {
        // Mapping tạm thời
        return switch (username) {
            case "customer1" -> 1L;
            case "customer2" -> 2L;
            case "admin" -> 1L; // Admin có thể xem như customer1
            default -> null;
        };
    }
}