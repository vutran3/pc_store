package com.pc.store.server.controllers;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.pc.store.server.dto.request.ApiResponse;
import com.pc.store.server.dto.request.ProductCreationRequest;
import com.pc.store.server.dto.response.ProductResponse;
import com.pc.store.server.entities.Product;
import com.pc.store.server.services.interf.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductController {
    ProductService productService;
    int size = 10;

    @GetMapping
    public ApiResponse<Page<Product>> getProducts(@RequestParam(defaultValue = "0") int page) {
        var products = productService.getProductsByPage(page, size);
        return ApiResponse.<Page<Product>>builder().result(products).build();
    }

    @GetMapping("asc")
    public ApiResponse<Page<Product>> getProductsAsc(@RequestParam(defaultValue = "0") int page) {
        var products = productService.getProductsByPageAsc(page, size);
        return ApiResponse.<Page<Product>>builder().result(products).build();
    }

    @GetMapping("desc")
    public ApiResponse<Page<Product>> getProductsDesc(@RequestParam(defaultValue = "0") int page) {
        var products = productService.getProductsByPageDesc(page, size);
        return ApiResponse.<Page<Product>>builder().result(products).build();
    }

    @GetMapping("/id")
    public ApiResponse<ProductResponse> getProductById(@RequestParam String id) {
        var product = productService.getProductById(id);
        return ApiResponse.<ProductResponse>builder().result(product).build();
    }

    @GetMapping("/{name}")
    public ApiResponse<Page<Product>> getProductByName(
            @PathVariable String name, @RequestParam(defaultValue = "0") int page) {
        var products = productService.getProductByName(name, page, size);
        return ApiResponse.<Page<Product>>builder().result(products).build();
    }

    @PostMapping("/add")
    public ApiResponse<ProductResponse> addProduct(@RequestBody ProductCreationRequest request) {
        var product = productService.addProduct(request);
        return ApiResponse.<ProductResponse>builder()
                .result(product.orElse(null))
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<java.util.List<ProductResponse>> getProductByNameOrSupplier(@RequestParam String keyword) {
        var products = productService.getProductByNameOrSupplier(keyword);
        return ApiResponse.<java.util.List<ProductResponse>>builder()
                .result(products)
                .build();
    }

    @PutMapping("/update/{productId}")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable String productId, @RequestBody ProductCreationRequest request) {
        var product = productService.updateProduct(productId, request);
        return ApiResponse.<ProductResponse>builder()
                .result(product.orElse(null))
                .build();
    }

    @DeleteMapping("/delete/{productId}")
    public ApiResponse<Boolean> deleteProduct(@PathVariable String productId) {
        var result = productService.deleteProductById(productId);
        return ApiResponse.<Boolean>builder().result(result).build();
    }
}
