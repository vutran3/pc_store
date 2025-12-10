package com.pc.store.server.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
public class PaymentResponse implements Serializable {
    String status;
    String message;
    String paymentId;
    String URL;
}
