package com.pc.store.server.services.impl;

import com.pc.store.server.dao.ProductDetailRepository;
import com.pc.store.server.dao.ProductRepository;
import com.pc.store.server.dto.request.ProductCreationRequest;
import com.pc.store.server.dto.response.ProductDetailResponse;
import com.pc.store.server.dto.response.ProductResponse;
import com.pc.store.server.entities.Product;
import com.pc.store.server.exception.AppException;
import com.pc.store.server.exception.ErrorCode;
import com.pc.store.server.mapper.ProductMapper;
import com.pc.store.server.services.interf.ProductDetailService;
import com.pc.store.server.services.interf.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProductDetailService productDetailService;

    @Override
    @SuppressWarnings("chua co logic check neu them 2 san pham giong nhau thi ntn")
    @Transactional
    public Optional<ProductResponse> addProduct(ProductCreationRequest request) {
        Product product = Optional.ofNullable(productMapper.toProduct(request))
                .orElseThrow(() -> new RuntimeException("Error mapping ProductCreationRequest to Product"));

        product.setUpdateDetail(false);
        Product savedProduct = productRepository.save(product);
        if (savedProduct == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_CREATED_SUCCESSFULLY);
        }
        log.info("Product created successfully with id: {}", savedProduct.getId());

        if (request.getProductDetailCreationRequest() != null) {
            try {
                ProductDetailResponse detailResponse = productDetailService.addProductDetail(
                        savedProduct,
                        request.getProductDetailCreationRequest()
                );

                if (detailResponse != null) {
                    savedProduct.setUpdateDetail(true);
                    savedProduct = productRepository.save(savedProduct);
                    log.info("Product detail linked successfully to product: {}", savedProduct.getId());
                }

            } catch (AppException e) {
                log.error("Failed to create product detail: {}", e.getMessage());
                throw e;
            }
        }

        return Optional.of(productMapper.toProductResponse(savedProduct));
    }

    @Override
    public List<ProductResponse> getProductByNameOrSupplier(String keyword) {
        List<ProductResponse> listProduct = Optional
                .ofNullable(productRepository.searchByNameOrSupplierName(keyword))
                .orElse(Collections.emptyList())
                .stream()
                .map(productMapper::toProductResponse).collect(Collectors.toList());
        return listProduct;
    }

    @Override
    public ProductResponse getProductById(String productId) {
        return productRepository.findById(new ObjectId(productId))
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    @Transactional
    public Optional<ProductResponse> updateProduct(String productId, ProductCreationRequest request) {
        Product existingProduct = productRepository.findById(new ObjectId(productId))
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Product updatedProduct = productMapper.toProduct(request);
        updatedProduct.setId(existingProduct.getId()); // Giữ nguyên ID cũ
        updatedProduct.setUpdateDetail(existingProduct.isUpdateDetail()); // Giữ nguyên flag

        Product savedProduct = productRepository.save(updatedProduct);
        if (savedProduct == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_UPDATED_SUCCESSFULLY);
        }

        log.info("Product updated successfully: {}", productId);

        if (request.getProductDetailCreationRequest() != null) {
            try {

                productDetailService.deleteProductDetailByProductId(productId);
                ProductDetailResponse detailResponse = productDetailService.addProductDetail(
                        savedProduct,
                        request.getProductDetailCreationRequest()
                );

                if (detailResponse != null) {
                    savedProduct.setUpdateDetail(true);
                    savedProduct = productRepository.save(savedProduct);
                    log.info("Product detail updated successfully for product: {}", productId);
                }

            } catch (Exception e) {
                log.error("Failed to update product detail: {}", e.getMessage());
                throw new AppException(ErrorCode.PRODUCT_DETAIL_UPDATE_FAILED);
            }
        }

        return Optional.of(productMapper.toProductResponse(savedProduct));
    }

    @Transactional
    @Override
    public boolean deleteProductById(String productId) {
        try {
            ObjectId id = new ObjectId(productId);

            productDetailService.deleteProductDetailByProductId(productId);
            log.info("Product detail deleted for product: {}", productId);

            productRepository.deleteById(id);
            log.info("Product deleted: {}", productId);
            boolean deleted = productRepository.findById(id).isEmpty();

            if (deleted) {
                log.info("Product and detail successfully deleted: {}", productId);
            }

            return deleted;

        } catch (Exception e) {
            log.error("Error deleting product {}: {}", productId, e.getMessage());
            throw new AppException(ErrorCode.PRODUCT_DELETE_FAILED);
        }
    }


    @Override
    public Page<Product> getProductsByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllBy(pageable);
    }

    @Override
    public Page<Product> getProductsByPageAsc(int page, int size) {
        Pageable pageable =
                PageRequest.of(page, size, Sort.by("priceAfterDiscount").ascending());
        return productRepository.findAllBy(pageable);
    }

    @Override
    public Page<Product> getProductsByPageDesc(int page, int size) {
        Pageable pageable =
                PageRequest.of(page, size, Sort.by("priceAfterDiscount").descending());
        return productRepository.findAllBy(pageable);
    }

    @Override
    public boolean updateInStockProduct(ObjectId productId, int quantity) {
        return false;
    }

    @Override
    public Page<Product> getProductByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByNameContaining(name, pageable);
    }

    @Override
    public Page<Product> getProductByName(String name) {
        return (Page<Product>) productRepository.findAllByName(name);
    }
}
