package com.pc.store.server.dto.request;

import com.pc.store.server.entities.CartItem;
import lombok.Data;
import java.util.List;

@Data
public class PaymentRequest {
    private String amount;
    private String userId;
    private String shipAddress;
    private List<CartItem> items;
}