package com.pc.store.server.mapper;

import org.mapstruct.Mapper;

import com.pc.store.server.dto.request.CustomerCreationResquest;
import com.pc.store.server.dto.response.CustomerResponse;
import com.pc.store.server.entities.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toCustomer(CustomerCreationResquest request);

    CustomerResponse toCustomerResponse(Customer customer);
}
