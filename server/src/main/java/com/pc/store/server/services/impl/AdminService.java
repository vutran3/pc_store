package com.pc.store.server.services.impl;

import com.pc.store.server.dao.*;
import com.pc.store.server.dto.request.CreationProductRequest;
import com.pc.store.server.dto.request.UpdateProductDetailReq;
import com.pc.store.server.dto.response.CustomerResponse;
import com.pc.store.server.dto.response.ProductDetailResponse;
import com.pc.store.server.dto.response.ProductResponse;
import com.pc.store.server.entities.*;
import com.pc.store.server.exception.AppException;
import com.pc.store.server.exception.ErrorCode;
import com.pc.store.server.mapper.CustomerMapper;
import com.pc.store.server.mapper.ProductDetailMapper;
import com.pc.store.server.mapper.ProductMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminService {
    final CustomerRepository customerRepository;
    final RoleRepository roleRepository;
    final ProductRepository productRepository;
    final CustomerMapper customerMapper;
    final ProductMapper productMapper;
    final ProductDetailMapper productDetailMapper;
    final MongoTemplate mongoTemplate;
    final ProductDetailRepository productDetailRepository;
    final OrderRepository orderRepository;
    final int SIZE = 10;
    private final GeminiService geminiService;

    @PostAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<Customer> getCustomersByPage(int page) {
        return customerRepository.findAllBy(PageRequest.of(page, SIZE));
    }

    @PostAuthorize("hasAuthority('ROLE_ADMIN')")
    public void updateRoleForUser(String userName, String roleName) {
        Role role = roleRepository.findById(roleName).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        mongoTemplate
                .update(Customer.class)
                .matching(Criteria.where("userName").is(userName).and("roles").nin(role))
                .apply(new Update().push("roles").value(role))
                .first();
    }

    public CustomerResponse getCustomerByUserName(String userName) {
        var customer = customerRepository
                .findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        return customerMapper.toCustomerResponse(customer);
    }

    public ProductResponse addProduct(CreationProductRequest request) {
        var product = productRepository.save(productMapper.toProductV2(request));
        product.setUpdateDetail(false);
        ProductDetail dt = ProductDetail.builder().product(product).build();
        productDetailRepository.save(dt);
        return productMapper.toProductResponse(product);
    }

    public ProductResponse updateProduct(CreationProductRequest request, String id) {
        var product = productRepository
                .findById(new ObjectId(id))
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        product.setImg(request.getImg());
        product.setName(request.getName());
        product.setDiscountPercent(request.getDiscountPercent());
        product.setInStock(request.getInStock());
        product.setPriceAfterDiscount(request.getPriceAfterDiscount());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setSupplier(new Supplier(
                request.getSupplier().getName(), request.getSupplier().getAddress()));
        productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    public ProductDetailResponse updateDetail(UpdateProductDetailReq request) {
        ProductDetail detail = productDetailRepository.findByProductId(new ObjectId(request.getProductId())).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_DETAIL_NOT_FOUND));
        Product product = productRepository
                .findById(new ObjectId(request.getProductId()))
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        product.setUpdateDetail(true);
        detail.setImages(request.getImages());
        detail.setProcessor(request.getProcessor());
        detail.setProduct(product);
        detail.setRam(request.getRam());
        detail.setStorage(request.getStorage());
        detail.setGraphicsCard(request.getGraphicsCard());
        detail.setPowerSupply(request.getPowerSupply());
        detail.setMotherboard(request.getMotherboard());
        detail.setCase_(request.getCase_());
        detail.setCoolingSystem(request.getCoolingSystem());
        detail.setOperatingSystem(request.getOperatingSystem());
        productRepository.save(product);
        productDetailRepository.save(detail);
        return productDetailMapper.toProductDetailResponse(detail);
    }

    public boolean deleteProduct(String id) {
        productRepository.deleteById(new ObjectId(id));
        return true;
    }

    public Page<Order> getOrders(int page) {
        return orderRepository.findAll(PageRequest.of(page, 5));
    }

    public Order updatePaymentStatus(String id) {
        Order order = orderRepository
                .findById(new ObjectId(id))
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setPaid(true);
        return orderRepository.save(order);
    }
}
