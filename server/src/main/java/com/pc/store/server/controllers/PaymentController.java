package com.pc.store.server.controllers;
import jakarta.servlet.http.HttpServletRequest;


import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pc.store.server.configurations.VNPayConfig;
import com.pc.store.server.dto.request.ApiResponse;
import com.pc.store.server.dto.request.PaymentRequest;
import com.pc.store.server.dto.response.PaymentResponse;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentController {

    @NonFinal
    @Value("${vnp.return.url}")
    public String vnp_ReturnUrl;

    @GetMapping("/create_payment")
    public ApiResponse<?> createPayment(HttpServletRequest request, @RequestParam(value = "amount") String amount)
            throws UnsupportedEncodingException {

        String orderType = "other";
        long amountInt = Integer.parseInt(amount) * 100L;

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPayConfig.getIpAddress(request);

        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountInt));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_OrderType", orderType);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 5);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        var payment = PaymentRequest.builder()
                .status("Ok")
                .message("Successfully")
                .URL(paymentUrl)
                .build();
        return ApiResponse.builder().result(payment).build();
    }

    @RequestMapping("/payment_status")
    public ApiResponse<PaymentResponse> getPaymentInfo(
            @RequestParam(value = "vnp_ResponseCode") String vnp_ResponseCode) {
        if (vnp_ResponseCode.equals("00")) {
            return ApiResponse.<PaymentResponse>builder()
                    .result(PaymentResponse.builder()
                            .status("YES")
                            .message("Successfully")
                            .build())
                    .build();
        } else {
            return ApiResponse.<PaymentResponse>builder()
                    .result(PaymentResponse.builder()
                            .status("NO")
                            .message("Failed")
                            .build())
                    .build();
        }
    }
}
