package com.pc.store.server.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pc.store.server.entities.CartItem;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderCreationRequest {
    String customerId;
    String shipAddress;
    List<CartItem> items;
    double totalPrice;
    String orderDate;
    String isPaid;
    String orderStatus;
}
