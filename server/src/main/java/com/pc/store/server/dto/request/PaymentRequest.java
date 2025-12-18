package com.pc.store.server.dto.request;

import java.util.List;

import com.pc.store.server.entities.CartItem;

import lombok.Data;

@Data
public class PaymentRequest {
    private String amount;
    private String userId;
    private String shipAddress;
    private List<CartItem> items;
}
