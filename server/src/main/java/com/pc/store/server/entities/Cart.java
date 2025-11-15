package com.pc.store.server.entities;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Document(collection = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {
    @Id
    ObjectId id;

    @DocumentReference
    @Field(name = "customerId")
    @Indexed(unique = true)
    Customer customer;

    List<CartItem> items;
}
