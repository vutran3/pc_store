package com.pc.store.server.dto.response;

import java.time.Instant;
import java.util.List;

import com.pc.store.server.entities.Customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    String id;
    String type; // GROUP, DIRECT
    String participantsHash;
    String conversationAvatar;
    String conversationName;
    List<Customer> participants;
    Instant createdDate;
    Instant modifiedDate;
}
