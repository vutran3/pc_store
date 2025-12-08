package iuh.fit.truongthanhtung_22637091_shopping.repository;

import iuh.fit.truongthanhtung_22637091_shopping.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderLines LEFT JOIN FETCH o.customer")
    List<Order> findAllWithOrderLines();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderLines LEFT JOIN FETCH o.customer WHERE o.id = :id")
    Optional<Order> findByIdWithOrderLines(Long id);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderLines LEFT JOIN FETCH o.customer WHERE o.customer.id = :customerId")
    List<Order> findByCustomerIdWithOrderLines(Long customerId);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderLines LEFT JOIN FETCH o.customer WHERE o.status = :status")
    List<Order> findByStatusWithOrderLines(String status);

    List<Order> findByCustomerId(Long customerId);
    List<Order> findByStatus(String status);
}
