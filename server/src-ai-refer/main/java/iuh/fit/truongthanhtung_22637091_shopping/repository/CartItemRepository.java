package iuh.fit.truongthanhtung_22637091_shopping.repository;

import iuh.fit.truongthanhtung_22637091_shopping.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}

