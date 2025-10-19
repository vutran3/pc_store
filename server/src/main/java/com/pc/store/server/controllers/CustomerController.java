package com.pc.store.server.controllers;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.pc.store.server.dto.request.ApiResponse;
import com.pc.store.server.dto.request.CustomerCreationResquest;
import com.pc.store.server.dto.response.CustomerResponse;
import com.pc.store.server.services.CustomerService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/customers")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomerController {
    CustomerService customerService;

    @PostMapping("/register")
    public ApiResponse<CustomerResponse> register(@Valid @RequestBody CustomerCreationResquest request) {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.createCustomer(request))
                .build();
    }

    @GetMapping("/{username}")
    public ApiResponse<CustomerResponse> getCustomerByID(@PathVariable String username) {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.getCustomer(username))
                .build();
    }

    @GetMapping("/info")
    public ApiResponse<CustomerResponse> getMyInfo() {
        log.info("Get my info");
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.getInfo())
                .build();
    }
}
