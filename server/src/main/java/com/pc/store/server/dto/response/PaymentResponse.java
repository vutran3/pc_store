package com.pc.store.server.dto.response;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
public class PaymentResponse implements Serializable {
    String status;
    String message;
    String URL;
}
