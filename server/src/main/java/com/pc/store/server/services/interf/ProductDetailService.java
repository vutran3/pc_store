package com.pc.store.server.services.interf;

import com.pc.store.server.dto.request.ProductDetailCreationRequest;
import com.pc.store.server.dto.response.ProductDetailResponse;
import com.pc.store.server.entities.Product;

public interface ProductDetailService {
    ProductDetailResponse getProductDetailById(String productId);

    ProductDetailResponse addProductDetail(Product product, ProductDetailCreationRequest request);

    boolean deleteProductDetailByProductId(String productId);
}
