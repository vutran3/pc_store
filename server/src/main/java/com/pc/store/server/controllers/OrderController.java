package com.pc.store.server.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import com.pc.store.server.dto.request.ApiResponse;
import com.pc.store.server.dto.request.OrderCreationRequest;
import com.pc.store.server.entities.Order;
import com.pc.store.server.services.impl.OrderService;
import com.pc.store.server.services.interf.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/orders")
public class OrderController {
    OrderService orderService;
    ProductService productService;

    @PostMapping
    public ApiResponse<Boolean> saveOrder(@RequestBody OrderCreationRequest request) {
        request.getItems().forEach(item -> {
            try {
                productService.updateInStockProduct(item.getProduct().getId(), item.getQuantity());
            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
            }
        });

        if (request.getOrderDate() == null)
            request.setOrderDate(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .format(LocalDateTime.now(ZoneId.systemDefault())));
        boolean result = orderService.saveOrder(request);
        return ApiResponse.<Boolean>builder().result(result).build();
    }

    @GetMapping("/{customerId}")
    public ApiResponse<List<Order>> getOrders(@PathVariable String customerId) {
        return ApiResponse.<List<Order>>builder()
                .result(orderService.getAllOrders(new ObjectId(customerId)))
                .build();
    }

    @PutMapping("/{orderId}")
    public ApiResponse<Order> updateOrderStatus(@PathVariable String orderId, @RequestParam String status) {
        var result = orderService.updateOrderStatus(new ObjectId(orderId), status);
        return ApiResponse.<Order>builder().result(result).build();
    }
}