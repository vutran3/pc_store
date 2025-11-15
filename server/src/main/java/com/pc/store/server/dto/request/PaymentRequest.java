package com.pc.store.server.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRequest implements Serializable {
    String status;
    String message;
    String URL;
}