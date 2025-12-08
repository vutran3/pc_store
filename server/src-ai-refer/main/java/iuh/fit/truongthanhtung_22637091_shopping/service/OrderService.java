package iuh.fit.truongthanhtung_22637091_shopping.service;

import iuh.fit.truongthanhtung_22637091_shopping.entity.Order;
import iuh.fit.truongthanhtung_22637091_shopping.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> findAll() {
        List<Order> orders = orderRepository.findAllWithOrderLines();
        // Recalculate total for each order
        orders.forEach(order -> {
            order.calculateTotalAmount();
        });
        return orders;
    }

    public Order findById(Long id) {
        Order order = orderRepository.findByIdWithOrderLines(id).orElse(null);
        if (order != null) {
            order.calculateTotalAmount();
        }
        return order;
    }

    public List<Order> findByCustomerId(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerIdWithOrderLines(customerId);
        orders.forEach(order -> order.calculateTotalAmount());
        return orders;
    }

    public List<Order> findByStatus(String status) {
        List<Order> orders = orderRepository.findByStatusWithOrderLines(status);
        orders.forEach(order -> order.calculateTotalAmount());
        return orders;
    }

    public Order save(Order order) {
        order.calculateTotalAmount();
        return orderRepository.save(order);
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    public Order updateStatus(Long id, String status) {
        Order order = findById(id);
        if (order != null) {
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }
}