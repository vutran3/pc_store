package com.pc.store.server.dao;

import com.pc.store.server.entities.Customer;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, ObjectId> {
    Optional<Customer> findByUserName(String userName);

    Page<Customer> findAllBy(Pageable pageable);

    boolean existsByUserName(String userName);
}
