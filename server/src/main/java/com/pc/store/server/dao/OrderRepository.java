package com.pc.store.server.dao;

import com.pc.store.server.entities.Order;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, ObjectId> {
    List<Order> findAllByCustomerId(ObjectId customerId);

    Page<Order> findAllBy(Pageable pageable);
}