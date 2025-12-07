
package com.pc.store.server.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.pc.store.server.dao.CartRepository;
import com.pc.store.server.dao.CustomerRespository;
import com.pc.store.server.dao.ProductRepository;
import com.pc.store.server.entities.Cart;
import com.pc.store.server.entities.CartItem;
import com.pc.store.server.entities.Customer;
import com.pc.store.server.exception.AppException;
import com.pc.store.server.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CartService {
    CustomerRespository customerRespository;
    CartRepository cartRepository;
    ProductRepository productRespository;

    public int getTotalQuantity(String customerId) {
        // Tìm Cart dựa trên customerId
        Cart cart = cartRepository
                .findByCustomerId(new ObjectId(customerId))
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        int count = 0;
        for (CartItem item : cart.getItems()) {
            count += 1;
        }
        // Tính tổng các sản phẩm CartItem trong giỏ hàng
        return count;
    }

    public Cart createNewCart(String customerId) {
        Customer customer = customerRespository
                .findById(new ObjectId(customerId))
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        return Cart.builder().customer(customer).items(new ArrayList<>()).build();
    }

    public Cart addOrUpdateCartItem(String customerId, String productId, int quantity) {
        Cart cart =
                cartRepository.findByCustomerId(new ObjectId(customerId)).orElseGet(() -> createNewCart(customerId));
        addOrUpdateItemInCart(cart, productId, quantity);
        return cartRepository.save(cart);
    }

    public Cart increaseQuantity(String customerId, String productId) {
        System.out.println(customerId);
        System.out.println(productId);
        Cart cart = cartRepository
                .findByCustomerId(new ObjectId(customerId))
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(new ObjectId(productId)))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
        existingItem.setQuantity(existingItem.getQuantity() + 1);
        return cartRepository.save(cart);
    }

    public Cart decreaseQuantity(String customerId, String productId) {
        Cart cart = cartRepository
                .findByCustomerId(new ObjectId(customerId))
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(new ObjectId(productId)))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
        if (existingItem.getQuantity() == 1) {
            cart.getItems().remove(existingItem);
        } else {
            existingItem.setQuantity(existingItem.getQuantity() - 1);
        }
        return cartRepository.save(cart);
    }

    private void addOrUpdateItemInCart(Cart cart, String productId, int quantity) {
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(new ObjectId(productId)))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {

            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(new ObjectId(productId))
                    .quantity(quantity)
                    .build();
            cart.getItems().add(newItem);
        }
    }

    public Cart removeCartItem(String customerId, String productId) {

        Cart cart = cartRepository
                .findByCustomerId(new ObjectId(customerId))
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(new ObjectId(productId)));

        // Lưu lại cart sau khi xóa
        return cartRepository.save(cart);
    }

    public Cart removeAllCartItems(String customerId) {
        // Tìm Cart dựa trên customerId
        Cart cart = cartRepository
                .findByCustomerId(new ObjectId(customerId))
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        // Xóa tất cả CartItem trong Cart
        cart.getItems().clear();
        return cartRepository.save(cart);
    }

    public List<String> getProductIdsByCustomerId(ObjectId customerId) {
        Cart cart = cartRepository
                .findByCustomerId(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        if (cart != null) {
            return cart.getItems().stream()
                    .map(cartItem -> cartItem.getProductId().toHexString())
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public List<CartItem> getCartItemsByCustomerId(ObjectId customerId) {
        Cart cart = cartRepository
                .findByCustomerId(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        if (cart != null) return cart.getItems();

        return List.of();
    }
}
