package com.pc.store.server.services.impl;

import com.pc.store.server.dao.ProductDetailRepository;
import com.pc.store.server.dto.request.ProductDetailCreationRequest;
import com.pc.store.server.dto.response.ProductDetailResponse;
import com.pc.store.server.entities.Product;
import com.pc.store.server.entities.ProductDetail;
import com.pc.store.server.exception.AppException;
import com.pc.store.server.exception.ErrorCode;
import com.pc.store.server.mapper.ProductDetailMapper;
import com.pc.store.server.mapper.ProductMapper;
import com.pc.store.server.services.interf.ProductDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailServiceImpl implements ProductDetailService {
    @Autowired
    ProductDetailMapper productDetailMapper;
    @Autowired
    ProductDetailRepository productDetailRepository;
    @Override
    public ProductDetailResponse getProductDetailById(String productId) {
        return productDetailRepository.findByProductId(new ObjectId(productId))
                .map(productDetailMapper::toProductDetailResponse)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_DETAIL_NOT_FOUND));
    }

    @Override
    public ProductDetailResponse addProductDetail(Product product, ProductDetailCreationRequest request) {
        try {
            ProductDetail productDetail = productDetailMapper.toProductDetail(request);
            productDetail.setProduct(product);
            productDetail.setImages(new ArrayList<>());

            ProductDetail savedDetail = productDetailRepository.save(productDetail);

            if (savedDetail == null) {
                throw new AppException(ErrorCode.PRODUCT_DETAIL_NOT_CREATED);
            }

            log.info("Product detail created successfully for product: {}", product.getId());

            // Map sang response
            return productDetailMapper.toProductDetailResponse(savedDetail);

        } catch (Exception e) {
            log.error("Error creating product detail for product {}: {}", product.getId(), e.getMessage());
            throw new AppException(ErrorCode.PRODUCT_DETAIL_NOT_CREATED);
        }
    }

    @Override
    public boolean deleteProductDetailByProductId(String productId) {
        try {
            ObjectId id = new ObjectId(productId);
            productDetailRepository.deleteByProductId(id);
            log.info("Product detail deleted for product: {}", productId);
            return true;
        } catch (Exception e) {
            log.error("Error deleting product detail for product {}: {}", productId, e.getMessage());
            return false;
        }
    }

}
