package com.pc.store.server.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pc.store.server.dto.request.ApiResponse;
import com.pc.store.server.dto.response.ProductDetailResponse;
import com.pc.store.server.services.interf.ProductDetailService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/product-detail")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductDetailController {
    ProductDetailService productDetailService;

    @GetMapping("/{productId}")
    public ApiResponse<ProductDetailResponse> getProductDetailById(@PathVariable String productId) {
        var productDetail = productDetailService.getProductDetailById(productId);
        return ApiResponse.<ProductDetailResponse>builder()
                .result(productDetail)
                .build();
    }
}
