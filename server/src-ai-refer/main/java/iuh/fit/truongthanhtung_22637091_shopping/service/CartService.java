package iuh.fit.truongthanhtung_22637091_shopping.service;

import iuh.fit.truongthanhtung_22637091_shopping.entity.Cart;
import iuh.fit.truongthanhtung_22637091_shopping.entity.Product;
import iuh.fit.truongthanhtung_22637091_shopping.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    /**
     * Lấy giỏ hàng của user (tạo mới nếu chưa có)
     */
    @Transactional
    public Cart getCartByUsername(String username) {
        return cartRepository.findByUsername(username)
                .orElseGet(() -> {
                    Cart newCart = new Cart(username);
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @Transactional
    public Cart addToCart(String username, Long productId, int quantity) {
        Cart cart = getCartByUsername(username);
        Product product = productService.findById(productId);

        if (product == null) {
            throw new RuntimeException("Sản phẩm không tồn tại!");
        }

        if (quantity <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0!");
        }

        cart.addItem(product, quantity);
        return cartRepository.save(cart);
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ
     */
    @Transactional
    public Cart updateCartItem(String username, Long productId, int quantity) {
        Cart cart = getCartByUsername(username);
        cart.updateItemQuantity(productId, quantity);
        return cartRepository.save(cart);
    }

    /**
     * Xóa sản phẩm khỏi giỏ
     */
    @Transactional
    public Cart removeFromCart(String username, Long productId) {
        Cart cart = getCartByUsername(username);
        cart.removeItem(productId);
        return cartRepository.save(cart);
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    @Transactional
    public void clearCart(String username) {
        Cart cart = getCartByUsername(username);
        cart.clear();
        cartRepository.save(cart);
    }

    /**
     * Lấy số lượng sản phẩm trong giỏ
     */
    public int getCartItemCount(String username) {
        return cartRepository.findByUsername(username)
                .map(Cart::getTotalItems)
                .orElse(0);
    }

    /**
     * Lấy tổng tiền trong giỏ
     */
    public Double getCartTotal(String username) {
        return cartRepository.findByUsername(username)
                .map(Cart::getTotalAmount)
                .orElse(0.0);
    }

//    Update so luong san pham sau khi thanh toan
    @Transactional
    public void updateProductStock(Long id, Integer quantity) {
        Product product = productService.findById(id);
        if (product != null) {
            int newStock = product.getStockQuantity() - quantity;
            if (newStock < 0) {
                throw new RuntimeException("Số lượng sản phẩm không đủ trong kho!");
            }
            product.setStockQuantity(newStock);
            productService.update(product.getId(), product);
        } else {
            throw new RuntimeException("Sản phẩm không tồn tại!");
        }
    }
}

