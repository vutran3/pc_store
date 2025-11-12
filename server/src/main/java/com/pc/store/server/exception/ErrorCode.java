package com.pc.store.server.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    CUSTOMER_EXISTED(1001, "Customer existed", HttpStatus.BAD_REQUEST),
    CUSTOMER_NOT_FOUND(1002, "Customer not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1003, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1004, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_KEY(1111, "Uncategorized error", HttpStatus.BAD_REQUEST),

    // Product & ProductDetail code 2xxx
    PRODUCT_NOT_CREATED_SUCCESSFULLY(2001, "Product not created successfully", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_NOT_FOUND(2002, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_UPDATED_SUCCESSFULLY(2003, "Product not created successfully", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_DETAIL_NOT_FOUND(2004, "Product detail not found", HttpStatus.NOT_FOUND),
    PRODUCT_DETAIL_NOT_CREATED(2005, "Product detail not created successfully", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_DELETE_FAILED(2006, "Product delete failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_DETAIL_UPDATE_FAILED(2007, "Product detail update failed", HttpStatus.INTERNAL_SERVER_ERROR),

    // Cart code
    CART_NOT_FOUND(3001, "Cart not found", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND(3002, "Cart item not found", HttpStatus.NOT_FOUND),

    //ORDER code
    ORDER_NOT_FOUND(4001, "Order not found", HttpStatus.NOT_FOUND),
    ;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
