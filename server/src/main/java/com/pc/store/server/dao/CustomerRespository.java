package com.pc.store.server.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.pc.store.server.entities.Customer;

@Repository
public interface CustomerRespository extends MongoRepository<Customer, String> {
    Optional<Customer> findByUserName(String userName);
}
