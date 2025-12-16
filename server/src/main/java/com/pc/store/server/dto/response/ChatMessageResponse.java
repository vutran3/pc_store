package com.pc.store.server.dto.response;

import java.time.Instant;

import com.pc.store.server.entities.Customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageResponse {
    String id;
    String conversationId;
    boolean me;
    String message;
    Customer sender;
    Instant createdDate;
}
