package iuh.fit.truongthanhtung_22637091_shopping.service;

import iuh.fit.truongthanhtung_22637091_shopping.entity.OrderLine;
import iuh.fit.truongthanhtung_22637091_shopping.repository.OrderLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderLineService {

    @Autowired
    private OrderLineRepository orderLineRepository;

    public List<OrderLine> findAll() {
        return orderLineRepository.findAll();
    }

    public OrderLine findById(Long id) {
        return orderLineRepository.findById(id).orElse(null);
    }

    public List<OrderLine> findByOrderId(Long orderId) {
        return orderLineRepository.findByOrderId(orderId);
    }

    public OrderLine save(OrderLine orderLine) {
        return orderLineRepository.save(orderLine);
    }

    public void deleteById(Long id) {
        orderLineRepository.deleteById(id);
    }

    public OrderLine update(Long id, OrderLine orderLine) {
        OrderLine existing = findById(id);
        if (existing != null) {
            existing.setQuantity(orderLine.getQuantity());
            existing.setPrice(orderLine.getPrice());
            return orderLineRepository.save(existing);
        }
        return null;
    }
}

