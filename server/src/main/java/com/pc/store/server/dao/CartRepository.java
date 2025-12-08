package com.pc.store.server.dao;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.pc.store.server.entities.Cart;

@Repository
public interface CartRepository extends MongoRepository<Cart, ObjectId> {
    Optional<Cart> findByCustomerId(ObjectId customerId);

    // Find all carts for a customer (to handle multiple carts)
    List<Cart> findAllByCustomerId(ObjectId customerId);

    // Find the most recent cart for a customer
    @Query("{ 'customer.id' : ?0 }")
    List<Cart> findCartsByCustomerId(ObjectId customerId);
}
