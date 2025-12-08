package iuh.fit.truongthanhtung_22637091_shopping.controller;

import iuh.fit.truongthanhtung_22637091_shopping.entity.Customer;
import iuh.fit.truongthanhtung_22637091_shopping.service.CustomerService;
import iuh.fit.truongthanhtung_22637091_shopping.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/customer")
@PreAuthorize("hasRole('ADMIN')") // Tất cả chức năng quản trị Customer chỉ dành cho ADMIN
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    // List all customers
    @GetMapping
    public String listCustomers(Model model) {
        List<Customer> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        return "customer/list";
    }

    // Search customers
    @GetMapping("/search")
    public String searchCustomers(@RequestParam("name") String name, Model model) {
        List<Customer> customers = customerService.searchByName(name);
        model.addAttribute("customers", customers);
        model.addAttribute("searchTerm", name);
        return "customer/list";
    }

    // Show customer detail with orders
    @GetMapping("/{id}")
    public String showCustomer(@PathVariable Long id, Model model) {
        Customer customer = customerService.findById(id);
        model.addAttribute("customer", customer);
        model.addAttribute("orders", orderService.findByCustomerId(id));
        return "customer/detail";
    }

    // Show create form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Customer customer = new Customer();
        customer.setCustomerSince(LocalDate.now()); // Set default date
        model.addAttribute("customer", customer);
        return "customer/form";
    }

    // Create customer
    @PostMapping
    public String createCustomer(@Valid @ModelAttribute Customer customer, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "customer/form";
        }

        // Set customerSince if not set
        if (customer.getCustomerSince() == null) {
            customer.setCustomerSince(LocalDate.now());
        }

        customerService.save(customer);
        return "redirect:/customer";
    }

    // Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Customer customer = customerService.findById(id);
        model.addAttribute("customer", customer);
        return "customer/form";
    }

    // Update customer
    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable Long id, @Valid @ModelAttribute Customer customer,
                                  BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            customer.setId(id); // Preserve ID for form
            return "customer/form";
        }

        customerService.update(id, customer);
        return "redirect:/customer";
    }

    // Delete customer
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerService.deleteById(id);
        return "redirect:/customer";
    }
}
