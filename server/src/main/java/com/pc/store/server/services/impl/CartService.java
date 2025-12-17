package com.pc.store.server.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pc.store.server.dao.CustomerRepository;
import com.pc.store.server.entities.Product;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.pc.store.server.dao.CartRepository;
import com.pc.store.server.dao.ProductRepository;
import com.pc.store.server.entities.Cart;
import com.pc.store.server.entities.CartItem;
import com.pc.store.server.entities.Customer;
import com.pc.store.server.entities.Product;
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
    CustomerRepository customerRespository;
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
        Cart cart = cartRepository
                .findByCustomerId(new ObjectId(customerId))
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(new ObjectId(productId)))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
        Product product = productRespository.findById(new ObjectId(productId)).orElse(null);

        if (product != null && product.getInStock() < existingItem.getQuantity()) {
            throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        } else {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
        }
        return cartRepository.save(cart);
    }

    public Cart decreaseQuantity(String customerId, String productId) {
        Cart cart = cartRepository
                .findByCustomerId(new ObjectId(customerId))
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(new ObjectId(productId)))
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
                .filter(item -> item.getProduct().getId().equals(new ObjectId(productId)))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {

            existingItem.setQuantity(existingItem.getQuantity() + 1);
        } else {
            Product product = productRespository
                    .findById(new ObjectId(productId))
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            CartItem newItem =
                    CartItem.builder().product(product).quantity(quantity).build();
            cart.getItems().add(newItem);
        }
    }

    public Cart removeCartItem(String customerId, String productId) {

        Cart cart = cartRepository
                .findByCustomerId(new ObjectId(customerId))
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        boolean removed =
                cart.getItems().removeIf(item -> item.getProduct().getId().equals(new ObjectId(productId)));

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
        // Lưu lại cart sau khi xóa
        return cartRepository.save(cart);
    }

    public List<String> getProductIdsByCustomerId(ObjectId customerId) {
        Cart cart = cartRepository
                .findByCustomerId(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        if (cart != null) {
            return cart.getItems().stream()
                    .map(cartItem -> cartItem.getProduct().getId().toHexString())
                    .collect(Collectors.toList());
        }
        return List.of(); // Trả về danh sách rỗng nếu giỏ hàng không tồn tại
    }

    public List<CartItem> getCartItemsByCustomerId(ObjectId customerId) {
        Cart cart = cartRepository
                .findByCustomerId(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        if (cart != null) {
            return cart.getItems();
        }
        return List.of(); // Trả về danh sách rỗng nếu giỏ hàng không tồn tại
    }
}
