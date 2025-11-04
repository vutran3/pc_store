package com.pc.store.server.mapper;

import com.pc.store.server.dto.request.ProductCreationRequest;
import com.pc.store.server.dto.response.ProductResponse;
import com.pc.store.server.entities.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ProductDetailMapper.class)
public interface ProductMapper {
    Product toProduct(ProductCreationRequest request);
    ProductResponse toProductResponse(Product product);
}
