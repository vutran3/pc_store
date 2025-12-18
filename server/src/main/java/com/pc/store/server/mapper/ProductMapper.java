package com.pc.store.server.mapper;

import com.pc.store.server.dto.request.ProductCreationRequest;
import org.mapstruct.Mapper;

import com.pc.store.server.dto.request.CreationProductRequest;
import com.pc.store.server.dto.response.ProductResponse;
import com.pc.store.server.entities.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProductV1(ProductCreationRequest response);
    Product toProductV2(CreationProductRequest response);

    ProductResponse toProductResponse(Product product);
}
