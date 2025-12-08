package com.pc.store.server.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.pc.store.server.entities.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, ObjectId> {
    @Query("{ '$or': [ {'name': { $regex: ?0, $options: 'i' }}, {'supplier.name': { $regex: ?0, $options: 'i' }} ] }")
    List<Product> searchByNameOrSupplierName(String keyword);

    Page<Product> findAllBy(Pageable pageable);

    Page<Product> findByNameContaining(String name, Pageable pageable);

    List<Product> findAllByName(String name);

    // Tìm sản phẩm theo khoảng giá
    @Query("{ 'priceAfterDiscount': { $lte: ?0 } }")
    Page<Product> findByPriceAfterDiscountLessThanEqual(double maxPrice, Pageable pageable);

    // Tìm sản phẩm theo khoảng giá và keyword
    @Query("{ 'name': { $regex: ?0, $options: 'i' }, 'priceAfterDiscount': { $lte: ?1 } }")
    Page<Product> findByNameContainingAndPriceLessThanEqual(String name, double maxPrice, Pageable pageable);

    // Tìm sản phẩm trong khoảng giá
    @Query("{ 'priceAfterDiscount': { $gte: ?0, $lte: ?1 } }")
    Page<Product> findByPriceAfterDiscountBetween(double minPrice, double maxPrice, Pageable pageable);
}
