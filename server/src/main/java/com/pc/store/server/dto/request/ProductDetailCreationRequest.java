package com.pc.store.server.dto.request;

import jakarta.validation.constraints.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProductDetailCreationRequest {

    //    @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "productId phải là ObjectId hợp lệ (24 ký tự hex)")
    String productId;

    //    @NotBlank(message = "Processor không được để trống")
    //    @Pattern(regexp = "^(Intel|AMD).*$", message = "Processor phải bắt đầu bằng Intel hoặc AMD")
    String processor;

    //    @NotBlank(message = "RAM không được để trống")
    //    @Pattern(regexp = "^[0-9]+GB$", message = "RAM phải có định dạng như 8GB, 16GB, ...")
    String ram;

    //    @NotBlank(message = "Storage không được để trống")
    //    @Pattern(regexp = "^[0-9]+(GB|TB)$", message = "Storage phải có định dạng như 512GB, 1TB,...")
    String storage;

    //    @NotBlank(message = "Graphics Card không được để trống")
    //    @Pattern(regexp = "^[A-Za-z0-9\\s\\-]+$", message = "Tên card đồ họa không hợp lệ")
    String graphicsCard;

    //    @NotBlank(message = "Nguồn điện không được để trống")
    //    @Pattern(regexp = "^[0-9]+W$", message = "Nguồn điện phải có định dạng như 500W, 750W,...")
    String powerSupply;

    //    @NotBlank(message = "Mainboard không được để trống")
    //    @Pattern(regexp = "^[A-Za-z0-9\\s\\-]+$", message = "Tên mainboard không hợp lệ")
    String motherboard;

    //    @Field("case")
    //    @NotBlank(message = "Case không được để trống")
    //    @Pattern(regexp = "^[A-Za-z0-9\\s\\-]+$", message = "Tên case không hợp lệ")
    String case_;

    //    @NotBlank(message = "Hệ thống tản nhiệt không được để trống")
    String coolingSystem;

    //    @NotBlank(message = "Hệ điều hành không được để trống")
    String operatingSystem;
}
