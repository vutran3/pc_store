package com.pc.store.server.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class Order {
    @Id
    @Field("_id")
    @JsonSerialize(using = ToStringSerializer.class)
    ObjectId id;

    @DocumentReference
    @Field("customer_id")
    Customer customer;

    String shipAddress;
    String orderDate;
    List<CartItem> items;
    double totalPrice;
    boolean isPaid;
    OrderStatus orderStatus;
}
