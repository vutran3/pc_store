package com.pc.store.server.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuthenticationRequest {
    @NotNull(message = "Tên đăng nhập không được để trống")
    @NotBlank(message = "Tên đăng nhập không được để trống")
    String userName;

    @NotNull(message = "Mật khẩu không được để trống")
    @NotBlank(message = "Mật khẩu không được để trống")
    @Min(value = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Max(value = 8, message = "Mật khẩu không được quá 8 ký tự")
    String password;
}
