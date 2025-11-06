package com.pc.store.server.entities;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItem {


    ObjectId productId;

    int quantity;


}