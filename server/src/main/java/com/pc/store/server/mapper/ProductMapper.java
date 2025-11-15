package com.pc.store.server.mapper;

import org.mapstruct.Mapper;

import com.pc.store.server.dto.request.ProductCreationRequest;
import com.pc.store.server.dto.response.ProductResponse;
import com.pc.store.server.entities.Product;

@Mapper(componentModel = "spring", uses = ProductDetailMapper.class)
public interface ProductMapper {
    Product toProduct(ProductCreationRequest request);

    ProductResponse toProductResponse(Product product);
}
