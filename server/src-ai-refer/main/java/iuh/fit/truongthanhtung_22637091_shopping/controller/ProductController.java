package iuh.fit.truongthanhtung_22637091_shopping.controller;

import iuh.fit.truongthanhtung_22637091_shopping.entity.Product;
import iuh.fit.truongthanhtung_22637091_shopping.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    // List all products - GUEST có thể xem
    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "product/list";
    }

    // Search products - GUEST có thể tìm kiếm
    @GetMapping("/search")
    public String searchProducts(@RequestParam("name") String name, Model model) {
        List<Product> products = productService.searchByName(name);
        model.addAttribute("products", products);
        model.addAttribute("searchTerm", name);
        return "product/list";
    }

    // Show product detail - GUEST có thể xem chi tiết
    @GetMapping("/detail/{id}")
    public String showProduct(@PathVariable Long id, Model model,
                             RedirectAttributes redirectAttributes) {
        Product product = productService.findById(id);

        if (product == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm với ID: " + id);
            return "redirect:/product";
        }

        model.addAttribute("product", product);
        return "product/detail";
    }

    // Show create form - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/form";
    }

    // Create product - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String createProduct(@Valid @ModelAttribute Product product,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "product/form";
        }
        productService.save(product);
        return "redirect:/product";
    }

    // Show edit form - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "product/form";
    }

    // Update product - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,
                               @Valid @ModelAttribute Product product,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            product.setId(id);
            return "product/form";
        }
        productService.update(id, product);
        return "redirect:/product";
    }

    // Delete product - Chỉ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/product";
    }
}