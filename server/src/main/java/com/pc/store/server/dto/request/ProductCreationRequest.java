package com.pc.store.server.dto.request;

import com.pc.store.server.entities.Supplier;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProductCreationRequest {

//    @NotBlank(message = "Tên sản phẩm không được để trống")
//    @Pattern(regexp = "^[A-Za-zÀ-ỹ0-9\\s\\-_,.()]+$", message = "Tên sản phẩm chỉ được chứa chữ, số và một số ký tự đặc biệt cơ bản")
    String name;

//    @NotBlank(message = "Ảnh sản phẩm không được để trống")
//    @Pattern(
//            regexp = "^(https?:\\/\\/)?([\\w\\-])+\\.[a-z]{2,6}([\\/\\w\\-\\.\\?\\=]*)?$",
//            message = "Ảnh phải là đường dẫn URL hợp lệ"
//    )
    String img;

//    @PositiveOrZero(message = "Giá sau giảm phải lớn hơn hoặc bằng 0")
    double priceAfterDiscount;

//    @Positive(message = "Giá gốc phải lớn hơn 0")
    double originalPrice;

//    @Min(value = 0, message = "Phần trăm giảm giá phải lớn hơn hoặc bằng 0")
//    @Max(value = 100, message = "Phần trăm giảm giá không được vượt quá 100%")
    double discountPercent;

//    @PositiveOrZero(message = "Số tiền giảm phải lớn hơn hoặc bằng 0")
    double priceDiscount;

//    @Min(value = 0, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
    int inStock;

//    @NotNull(message = "Nhà cung cấp không được để trống")
    Supplier supplier;

//    @Valid
//    @NotNull(message = "Thông tin chi tiết sản phẩm không được để trống")
    ProductDetailCreationRequest productDetailCreationRequest;
}
