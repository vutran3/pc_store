package com.pc.store.server.controllers;

import java.util.Base64;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pc.store.server.dto.request.ApiResponse;
import com.pc.store.server.dto.request.CreationProductRequest;
import com.pc.store.server.dto.request.UpdateProductDetailReq;
import com.pc.store.server.dto.response.ProductDetailResponse;
import com.pc.store.server.dto.response.ProductResponse;
import com.pc.store.server.entities.Customer;
import com.pc.store.server.entities.Order;
import com.pc.store.server.exception.AppException;
import com.pc.store.server.exception.ErrorCode;
import com.pc.store.server.services.impl.AdminService;
import com.pc.store.server.services.impl.GeminiService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {

    AdminService adminService;
    Cloudinary cloudinary;
    private final GeminiService geminiService;

    @GetMapping("/customers")
    public ApiResponse<Page<Customer>> getAllCustomers(@RequestParam(defaultValue = "0") int page) {
        return ApiResponse.<Page<Customer>>builder()
                .result(adminService.getCustomersByPage(page))
                .build();
    }

    @PostMapping("/update-role/{userName}")
    public ApiResponse<Void> updateRoleForUser(@PathVariable String userName) {
        log.info("Update role for user: " + userName);
        adminService.updateRoleForUser(userName, "ADMIN");
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/add-product")
    public ApiResponse<ProductResponse> addProduct(@RequestBody CreationProductRequest request) {
        try {
            String base64Image = request.getImg();

            if (!geminiService.isImageSafe(base64Image)) {
                throw new AppException(ErrorCode.SENSITIVE_IMAGE_CONTENT);
            }

            if (base64Image.startsWith("data:image")) {
                base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
            }
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Upload lên Cloudinary
            String imageUrl = cloudinary
                    .uploader()
                    .upload(
                            imageBytes,
                            ObjectUtils.asMap(
                                    "resource_type", "image",
                                    "folder", "PC_Store"))
                    .get("url")
                    .toString();

            request.setImg(imageUrl);
            var result = adminService.addProduct(request);
            return ApiResponse.<ProductResponse>builder().result(result).build();
        } catch (AppException e) {
            throw e; // Ném lại lỗi của Gemini nếu có
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.UPLOAD_IMAGE_FAILED);
        }
    }

    @PutMapping("/update-product/{id}")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable String id, @RequestBody CreationProductRequest request) {
        try {
            String base64Image = request.getImg();
            String imageUrl;

            if (base64Image.startsWith("http://") || base64Image.startsWith("https://")) {
                imageUrl = base64Image;
            } else if (base64Image.startsWith("data:image")) {

                if (!geminiService.isImageSafe(base64Image)) {
                    throw new AppException(ErrorCode.SENSITIVE_IMAGE_CONTENT);
                }

                String rawBase64 = base64Image.substring(base64Image.indexOf(",") + 1);
                byte[] imageBytes = Base64.getDecoder().decode(rawBase64);

                imageUrl = cloudinary
                        .uploader()
                        .upload(
                                imageBytes,
                                ObjectUtils.asMap(
                                        "resource_type", "image",
                                        "folder", "PC_Store"))
                        .get("url")
                        .toString();
            } else {
                throw new AppException(ErrorCode.UPLOAD_IMAGE_FAILED);
            }

            request.setImg(imageUrl);
            var result = adminService.updateProduct(request, id);
            return ApiResponse.<ProductResponse>builder().result(result).build();
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.UPLOAD_IMAGE_FAILED);
        }
    }

    @PutMapping("/update-product-detail")
    public ApiResponse<ProductDetailResponse> updateDetail(@RequestBody UpdateProductDetailReq request) {
        try {
            for (String base64Image : request.getImagesUpload()) {
                if (base64Image.startsWith("data:image")) {
                    base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
                }
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                String imageUrl = cloudinary
                        .uploader()
                        .upload(
                                imageBytes,
                                ObjectUtils.asMap(
                                        "resource_type", "image",
                                        "folder", "PC_Store"))
                        .get("url")
                        .toString();
                request.getImages().add(imageUrl);
            }
            var result = adminService.updateDetail(request);
            return ApiResponse.<ProductDetailResponse>builder().result(result).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.UPLOAD_IMAGE_FAILED);
        }
    }

    @DeleteMapping("/delete-product/{id}")
    public ApiResponse<Boolean> deleteProduct(@PathVariable String id) {
        boolean result = adminService.deleteProduct(id);
        return ApiResponse.<Boolean>builder().result(result).build();
    }

    @GetMapping("/list-orders")
    public ApiResponse<Page<Order>> getOrders(@RequestParam(defaultValue = "0") int page) {
        var result = adminService.getOrders(page);
        return ApiResponse.<Page<Order>>builder().result(result).build();
    }

    @PutMapping("/update-payment-status/{id}")
    public ApiResponse<Order> updatePaymentStatus(@PathVariable String id) {
        return ApiResponse.<Order>builder()
                .result(adminService.updatePaymentStatus(id))
                .build();
    }
}
