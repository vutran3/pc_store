package com.pc.store.server.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import com.pc.store.server.dao.ProductRepository;
import com.pc.store.server.dto.request.ApiResponse;
import com.pc.store.server.entities.Cart;
import com.pc.store.server.entities.CartItem;
import com.pc.store.server.entities.Product;
import com.pc.store.server.services.CartService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/cart")
public class CartController {
    CartService cartService;
    ProductRepository productRepository; // <-- inject product repo

    @GetMapping("countOfItems")
    public ApiResponse<Integer> countOfItems(@RequestParam String customerId) {
        var result = cartService.getTotalQuantity(customerId);
        return ApiResponse.<Integer>builder().result(result).build();
    }

    @PostMapping("/createCart/{customerId}")
    public ApiResponse<Cart> createCart(@PathVariable String customerId) {
        var result = cartService.createNewCart(customerId);
        return ApiResponse.<Cart>builder().result(result).build();
    }

    @PostMapping("/{customerId}/addCart")
    public ApiResponse<Cart> addCart(
            @PathVariable String customerId, @RequestParam String productId, @RequestParam int quantity) {
        var result = cartService.addOrUpdateCartItem(customerId, productId, quantity);
        return ApiResponse.<Cart>builder().result(result).build();
    }

    @PostMapping("/increaseQuantity")
    public ApiResponse<Cart> increaseQuantity(@RequestParam String customerId, @RequestParam String productId) {
        var result = cartService.increaseQuantity(customerId, productId);
        return ApiResponse.<Cart>builder().result(result).build();
    }

    @PostMapping("/decreaseQuantity")
    public ApiResponse<Cart> decreaseQuantity(@RequestParam String customerId, @RequestParam String productId) {
        var result = cartService.decreaseQuantity(customerId, productId);
        return ApiResponse.<Cart>builder().result(result).build();
    }

    @DeleteMapping("/deleteItem")
    public ApiResponse<Cart> deleteItem(@RequestParam String customerId, @RequestParam String productId) {
        var result = cartService.removeCartItem(customerId, productId);
        return ApiResponse.<Cart>builder().result(result).build();
    }

    @DeleteMapping("/deleteCart")
    public ApiResponse<String> deleteCart(@RequestParam String customerId) {
        cartService.removeAllCartItems(customerId);
        return ApiResponse.<String>builder().result("Cart deleted successfully").build();
    }

    @GetMapping("/productIds/{customerId}")
    public ApiResponse<List<String>> getProductIdsByCustomerId(@PathVariable String customerId) {
        List<String> productIds = cartService.getProductIdsByCustomerId(new ObjectId(customerId));
        return ApiResponse.<List<String>>builder().result(productIds).build();
    }

    @GetMapping("/items/{customerId}")
    public ApiResponse<List<Map<String, Object>>> getCartItemsByCustomerId(@PathVariable String customerId) {
        // Lấy CartItem (productId + quantity)
        List<CartItem> cartItems = cartService.getCartItemsByCustomerId(new ObjectId(customerId));

        // Với mỗi CartItem, lấy product detail từ ProductRepository và build response object
        List<Map<String, Object>> itemsWithProduct = cartItems.stream()
                .map(item -> {
                    Map<String, Object> m = new HashMap<>();
                    Product product =
                            productRepository.findById(item.getProductId()).orElse(null);
                    m.put("product", product); // full product details (null nếu không tìm thấy)
                    m.put("productId", item.getProductId().toHexString());
                    m.put("quantity", item.getQuantity());
                    return m;
                })
                .collect(Collectors.toList());

        return ApiResponse.<List<Map<String, Object>>>builder()
                .result(itemsWithProduct)
                .build();
    }
}
