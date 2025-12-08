package iuh.fit.truongthanhtung_22637091_shopping.repository;

import iuh.fit.truongthanhtung_22637091_shopping.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductId(Long productId);
}
