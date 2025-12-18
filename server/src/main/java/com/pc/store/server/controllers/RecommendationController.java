package com.pc.store.server.controllers;

import com.pc.store.server.dto.request.ApiResponse;
import com.pc.store.server.entities.Product;
import com.pc.store.server.services.impl.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{customerId}")
    public ApiResponse<List<Product>> getRecommendations(@PathVariable String customerId) {
        // Lấy 4 sản phẩm để hiển thị trên UI
        List<Product> products = recommendationService.getRecommendedProducts(customerId, 4);
        return ApiResponse.<List<Product>>builder()
                .result(products)
                .build();
    }
}