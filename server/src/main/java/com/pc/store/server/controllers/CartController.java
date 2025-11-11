package com.pc.store.server.controllers;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import com.pc.store.server.dto.request.ApiResponse;
import com.pc.store.server.entities.Cart;
import com.pc.store.server.entities.CartItem;
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
    public ApiResponse<List<CartItem>> getCartItemsByCustomerId(@PathVariable String customerId) {
        List<CartItem> cartItems = cartService.getCartItemsByCustomerId(new ObjectId(customerId));
        return ApiResponse.<List<CartItem>>builder().result(cartItems).build();
    }
}
