package com.pc.store.server.dao;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.pc.store.server.entities.Payment;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, ObjectId> {
    Payment findPaymentsByPaymentId(String paymentId);
}
