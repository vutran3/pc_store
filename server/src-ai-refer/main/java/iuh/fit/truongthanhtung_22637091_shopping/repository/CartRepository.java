package iuh.fit.truongthanhtung_22637091_shopping.repository;

import iuh.fit.truongthanhtung_22637091_shopping.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUsername(String username);
    void deleteByUsername(String username);
}

