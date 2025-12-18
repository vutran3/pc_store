package com.pc.store.server.mapper;

import org.mapstruct.Mapper;

import com.pc.store.server.dto.request.ProductDetailCreationRequest;
import com.pc.store.server.dto.response.ProductDetailResponse;
import com.pc.store.server.entities.ProductDetail;

@Mapper(componentModel = "spring")
public interface ProductDetailMapper {
    ProductDetail toProductDetail(ProductDetailCreationRequest request);

    ProductDetailResponse toProductDetailResponse(ProductDetail productDetail);
}
