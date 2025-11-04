package com.pc.store.server.dao;

import com.pc.store.server.entities.Product;
import com.pc.store.server.entities.ProductDetail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductDetailRepository extends MongoRepository<ProductDetail, ObjectId> {
    Optional<ProductDetail> findByProductId(ObjectId productId);
    void deleteByProductId(ObjectId productId);
}
