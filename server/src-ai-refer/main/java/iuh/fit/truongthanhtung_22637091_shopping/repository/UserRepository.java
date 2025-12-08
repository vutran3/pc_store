package iuh.fit.truongthanhtung_22637091_shopping.repository;

import iuh.fit.truongthanhtung_22637091_shopping.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}