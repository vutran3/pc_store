package com.pc.store.server.dao;

import com.pc.store.server.entities.Cart;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, ObjectId> {
    Optional<Cart> findByCustomerId(ObjectId customerId);
}