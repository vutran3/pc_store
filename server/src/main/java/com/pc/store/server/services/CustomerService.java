package com.pc.store.server.services;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pc.store.server.dao.CustomerRespository;
import com.pc.store.server.dto.request.AddressRequest;
import com.pc.store.server.dto.request.CustomerCreationResquest;
import com.pc.store.server.dto.response.CustomerResponse;
import com.pc.store.server.entities.Address;
import com.pc.store.server.entities.Customer;
import com.pc.store.server.exception.AppException;
import com.pc.store.server.exception.ErrorCode;
import com.pc.store.server.mapper.CustomerMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerService {

    final CustomerRespository customerRespository;
    final MongoTemplate mongoTemplate;
    final PasswordEncoder passwordEncoder;
    final CustomerMapper customerMapper;

    public CustomerResponse createCustomer(CustomerCreationResquest customerCreationResquest) {
        synchronized (this) {
            Query query = new Query();
            query.addCriteria(Criteria.where("useName").is(customerCreationResquest.getUserName()));
            if (mongoTemplate.exists(query, Customer.class)) {
                throw new AppException(ErrorCode.CUSTOMER_EXISTED);
            }
            Update update = new Update();
            update.set("userName", customerCreationResquest.getUserName());
            update.set("firstName", customerCreationResquest.getFirstName());
            update.set("lastName", customerCreationResquest.getLastName());
            update.set("email", customerCreationResquest.getEmail());
            update.set("phoneNumber", customerCreationResquest.getPhoneNumber());
            update.set("password", passwordEncoder.encode(customerCreationResquest.getPassword()));
            update.set("addresses", new HashSet<Address>());
            Customer customer = mongoTemplate.findAndModify(
                    query,
                    update,
                    FindAndModifyOptions.options().returnNew(true).upsert(true),
                    Customer.class);
            return customerMapper.toCustomerResponse(customer);
        }
    }

    public CustomerResponse getCustomer(String userName) {
        Customer customer = customerRespository
                .findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        log.info("UserName:{}", customer.getUserName());
        return customerMapper.toCustomerResponse(customer);
    }

    public CustomerResponse getInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        log.info(name);
        Customer customer = customerRespository
                .findByUserName(name)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        return customerMapper.toCustomerResponse(customer);
    }

    public CustomerResponse addAddress(AddressRequest request) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Add address for user: {}", name);

        Customer customer = customerRespository
                .findByUserName(name)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        log.info("Customer found: {}", customer.toString());
        Optional<Address> existingAddress = customer.getAddresses().stream()
                .filter(addr -> addr.getAddress().equalsIgnoreCase(request.getAddress()))
                .findFirst();

        if (existingAddress.isPresent()) {
            // Nếu địa chỉ đã có và chỉ muốn update default flag
            if (request.isDefault()) {
                customer.getAddresses().forEach(a -> a.setDefault(false));
                existingAddress.get().setDefault(true);
            }
            // Lưu trạng thái mới
            customerRespository.save(customer);
            return customerMapper.toCustomerResponse(customer);
        }

        // Nếu là địa chỉ mới
        if (request.isDefault()) {
            customer.getAddresses().forEach(a -> a.setDefault(false));
        }

        Address newAddress = Address.builder()
                .address(request.getAddress())
                .isDefault(request.isDefault())
                .build();

        customer.getAddresses().add(newAddress);

        customerRespository.save(customer);
        return customerMapper.toCustomerResponse(customer);
    }
}
