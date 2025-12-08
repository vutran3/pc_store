package iuh.fit.truongthanhtung_22637091_shopping.controller;

import iuh.fit.truongthanhtung_22637091_shopping.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller xử lý các request liên quan đến AI Assistant
 */
@Controller
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * Hiển thị trang AI Chat
     */
    @GetMapping("/chat")
    public String showChatPage() {
        return "ai/chat";
    }

    /**
     * API endpoint để xử lý câu hỏi từ người dùng
     */
    @PostMapping("/ask")
    @ResponseBody
    public ResponseEntity<Map<String, String>> askQuestion(@RequestBody Map<String, String> request) {
        try {
            String question = request.get("question");

            if (question == null || question.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Câu hỏi không được để trống"));
            }

            String answer = aiService.processQuery(question);

            Map<String, String> response = new HashMap<>();
            response.put("answer", answer);
            response.put("status", "success");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Có lỗi xảy ra: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * API endpoint để lấy thống kê nhanh
     */
    @GetMapping("/stats")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getStats() {
        try {
            String stats = aiService.processQuery("Cho tôi biết thống kê tổng quan về hệ thống");
            return ResponseEntity.ok(Map.of("stats", stats, "status", "success"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage(), "status", "error"));
        }
    }
}

