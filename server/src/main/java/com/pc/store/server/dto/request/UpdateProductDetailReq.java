package com.pc.store.server.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductDetailReq {
    String id;
    List<String> images;
    String processor;
    String ram;
    String storage;
    String graphicsCard;
    String productId;
    String powerSupply;
    String motherboard;
    String case_;
    String coolingSystem;
    String operatingSystem;
    List<String> imagesUpload;
}
