package iuh.fit.truongthanhtung_22637091_shopping.controller;

import iuh.fit.truongthanhtung_22637091_shopping.entity.Order;
import iuh.fit.truongthanhtung_22637091_shopping.entity.OrderLine;
import iuh.fit.truongthanhtung_22637091_shopping.entity.Product;
import iuh.fit.truongthanhtung_22637091_shopping.service.OrderLineService;
import iuh.fit.truongthanhtung_22637091_shopping.service.OrderService;
import iuh.fit.truongthanhtung_22637091_shopping.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orderline")
public class OrderLineController {

    @Autowired
    private OrderLineService orderLineService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    // Show form to add orderline - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add/{orderId}")
    public String showAddForm(@PathVariable Long orderId, Model model) {
        Order order = orderService.findById(orderId);
        model.addAttribute("order", order);
        model.addAttribute("products", productService.findAll());
        model.addAttribute("orderLine", new OrderLine());
        return "orderline/form";
    }

    // Add orderline to order - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add/{orderId}")
    public String addOrderLine(@PathVariable Long orderId,
                              @RequestParam Long productId,
                              @RequestParam Integer quantity) {
        Order order = orderService.findById(orderId);
        Product product = productService.findById(productId);

        OrderLine orderLine = new OrderLine(order, product, quantity);
        orderLineService.save(orderLine);

        // Recalculate order total
        order.calculateTotalAmount();
        orderService.save(order);

        return "redirect:/order/detail/" + orderId;
    }

    // Show edit form - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        OrderLine orderLine = orderLineService.findById(id);
        model.addAttribute("orderLine", orderLine);
        return "orderline/edit";
    }

    // Update orderline - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/{id}")
    public String updateOrderLine(@PathVariable Long id,
                                 @RequestParam Integer quantity,
                                 @RequestParam Double price) {
        OrderLine orderLine = orderLineService.findById(id);
        orderLine.setQuantity(quantity);
        orderLine.setPrice(price);
        orderLineService.save(orderLine);

        // Recalculate order total
        Order order = orderLine.getOrder();
        order.calculateTotalAmount();
        orderService.save(order);

        return "redirect:/order/detail/" + orderLine.getOrder().getId();
    }

    // Delete orderline - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteOrderLine(@PathVariable Long id) {
        OrderLine orderLine = orderLineService.findById(id);
        Long orderId = orderLine.getOrder().getId();

        orderLineService.deleteById(id);

        // Recalculate order total
        Order order = orderService.findById(orderId);
        order.calculateTotalAmount();
        orderService.save(order);

        return "redirect:/order/detail/" + orderId;
    }

    // View orderline detail - CUSTOMER và ADMIN có thể xem
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/detail/{id}")
    public String showOrderLineDetail(@PathVariable Long id, Model model) {
        OrderLine orderLine = orderLineService.findById(id);
        model.addAttribute("orderLine", orderLine);
        return "orderline/detail";
    }
}
