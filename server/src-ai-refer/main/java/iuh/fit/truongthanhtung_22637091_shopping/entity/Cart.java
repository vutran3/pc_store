package iuh.fit.truongthanhtung_22637091_shopping.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity giỏ hàng - mỗi user có 1 giỏ hàng
 */
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username; // Username của user sở hữu giỏ hàng

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime lastModified;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> items = new ArrayList<>();

    public Cart() {
        this.createdDate = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    public Cart(String username) {
        this();
        this.username = username;
    }

    // Thêm sản phẩm vào giỏ
    public void addItem(Product product, int quantity) {
        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        CartItem existingItem = items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Nếu đã có, tăng số lượng
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Nếu chưa có, thêm mới
            CartItem newItem = new CartItem(this, product, quantity);
            items.add(newItem);
        }
        this.lastModified = LocalDateTime.now();
    }

    // Xóa sản phẩm khỏi giỏ
    public void removeItem(Long productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
        this.lastModified = LocalDateTime.now();
    }

    // Cập nhật số lượng
    public void updateItemQuantity(Long productId, int quantity) {
        items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    if (quantity <= 0) {
                        removeItem(productId);
                    } else {
                        item.setQuantity(quantity);
                        this.lastModified = LocalDateTime.now();
                    }
                });
    }

    // Xóa tất cả sản phẩm
    public void clear() {
        items.clear();
        this.lastModified = LocalDateTime.now();
    }

    // Tính tổng tiền
    public Double getTotalAmount() {
        return items.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    // Đếm tổng số lượng sản phẩm
    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
}

