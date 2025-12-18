package com.pc.store.server.dao;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.pc.store.server.entities.ProductDetail;

@Repository
public interface ProductDetailRepository extends MongoRepository<ProductDetail, ObjectId> {
    Optional<ProductDetail> findByProductId(ObjectId productId);

    void deleteByProductId(ObjectId productId);
}
