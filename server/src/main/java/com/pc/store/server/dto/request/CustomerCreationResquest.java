package com.pc.store.server.dto.request;

import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerCreationResquest {
    @NotNull(message = "Tên đăng nhập không được để trống")
    @NotBlank(message = "Tên đăng nhập không được để trống")
    String userName;

    @NotNull(message = "Họ không được để trống")
    String firstName;

    @NotNull(message = "Tên không được để trống")
    String lastName;

    @NotNull(message = "Email không được để trống")
    String email;

    @NotNull(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại gồm 10 số và bắt đầu bằng 0 ")
    String phoneNumber;

    @NotNull(message = "Mật khẩu không được để trống")
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 5, message = "Mật khẩu phải có ít nhất 5 ký tự")
    String password;
}
