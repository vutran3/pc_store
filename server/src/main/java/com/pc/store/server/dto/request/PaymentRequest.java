package com.pc.store.server.dto.request;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
public class PaymentRequest implements Serializable {
    String status;
    String message;
    String URL;
}
