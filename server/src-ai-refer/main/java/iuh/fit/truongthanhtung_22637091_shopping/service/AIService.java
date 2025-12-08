package iuh.fit.truongthanhtung_22637091_shopping.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service xử lý các yêu cầu AI để tra cứu thông tin từ database
 */
@Service
public class AIService {

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Xử lý câu hỏi từ người dùng và trả về câu trả lời
     */
    public String processQuery(String userQuestion) {
        try {
            // Kiểm tra kết nối database
            String statisticsData = getStatisticsData();

            if (statisticsData.contains("Không thể lấy dữ liệu")) {
                return "⚠️ Không thể kết nối đến cơ sở dữ liệu. Vui lòng kiểm tra:\n" +
                        "1. MariaDB đang chạy trên port 3388\n" +
                        "2. Database 'shoppingdata' đã được tạo\n" +
                        "3. Username/password trong application.properties đúng";
            }

            // Kiểm tra nếu câu hỏi về sản phẩm cụ thể (tìm kiếm trực tiếp)
            String directSearchResult = searchProductDirectly(userQuestion);
            if (directSearchResult != null) {
                return directSearchResult;
            }

            // Lấy thông tin về cấu trúc database
            String databaseContext = getDatabaseContext();

            // Tạo prompt với context
            String systemPrompt = """
                    Bạn là trợ lý AI cho hệ thống Shopping.
                    Bạn có quyền truy cập vào database với các bảng sau:
                    
                    %s
                    
                    Hãy trả lời câu hỏi của người dùng dựa trên thông tin database được cung cấp.
                    Trả lời bằng tiếng Việt, ngắn gọn, dễ hiểu và chuyên nghiệp.
                    Sử dụng emoji để làm cho câu trả lời sinh động hơn.
                    Nếu thông tin có trong dữ liệu thống kê, hãy trả lời dựa trên đó.
                    """.formatted(databaseContext);

            String fullPrompt = systemPrompt + "\n\nDữ liệu thống kê hiện tại:\n" + statisticsData +
                    "\n\nCâu hỏi: " + userQuestion;

            // Gọi OpenAI API
            ChatClient chatClient = chatClientBuilder.build();

            return chatClient.prompt()
                    .user(fullPrompt)
                    .call()
                    .content();

        } catch (Exception e) {
            // Log chi tiết lỗi để debug
            e.printStackTrace();

            // Trả về thông báo lỗi thân thiện
            if (e.getMessage() != null && e.getMessage().contains("API key")) {
                return "❌ Lỗi API Key: Vui lòng kiểm tra OpenAI API key.\n" +
                        "Cách khắc phục:\n" +
                        "1. Lấy API key tại: https://platform.openai.com/\n" +
                        "2. Set biến môi trường: set OPENAI_API_KEY=sk-your-key\n" +
                        "3. Restart ứng dụng";
            } else if (e.getMessage() != null && e.getMessage().contains("ChatClient")) {
                return "❌ Lỗi cấu hình Spring AI. Vui lòng kiểm tra:\n" +
                        "1. Dependencies trong pom.xml\n" +
                        "2. File AIConfig.java đã được tạo\n" +
                        "3. Chạy: mvn clean install";
            } else {
                return "❌ Xin lỗi, có lỗi xảy ra: " + e.getMessage() + "\n\n" +
                        "💡 Gợi ý:\n" +
                        "- Kiểm tra log console để xem chi tiết lỗi\n" +
                        "- Đảm bảo đã cài đặt OpenAI API key\n" +
                        "- Kiểm tra kết nối internet và database";
            }
        }
    }

    /**
     * Tìm kiếm sản phẩm trực tiếp từ database khi phát hiện từ khóa
     */
    private String searchProductDirectly(String question) {
        try {
            String lowerQuestion = question.toLowerCase();

            // Danh sách từ khóa tìm kiếm sản phẩm
            String[] searchKeywords = {"iphone", "samsung", "laptop", "macbook", "airpods",
                                       "ipad", "apple watch", "galaxy", "sony", "logitech",
                                       "sản phẩm", "có", "bán", "giá"};

            boolean isProductSearch = false;
            for (String keyword : searchKeywords) {
                if (lowerQuestion.contains(keyword)) {
                    isProductSearch = true;
                    break;
                }
            }

            if (!isProductSearch) {
                return null; // Không phải câu hỏi về sản phẩm
            }

            // Tìm kiếm trong database với LIKE
            StringBuilder searchResult = new StringBuilder();

            // Lấy từ khóa để search (lấy các từ quan trọng)
            String searchTerm = extractSearchTerm(lowerQuestion);

            if (searchTerm != null && !searchTerm.isEmpty()) {
                List<Map<String, Object>> products = jdbcTemplate.queryForList(
                    "SELECT * FROM products WHERE LOWER(name) LIKE ? OR LOWER(description) LIKE ? LIMIT 10",
                    "%" + searchTerm + "%", "%" + searchTerm + "%"
                );

                if (!products.isEmpty()) {
                    searchResult.append("🔍 Tìm thấy ").append(products.size())
                               .append(" sản phẩm liên quan đến \"").append(searchTerm).append("\":\n\n");

                    for (int i = 0; i < products.size(); i++) {
                        Map<String, Object> product = products.get(i);
                        searchResult.append((i + 1)).append(". 📦 **")
                                   .append(product.get("name")).append("**\n");
                        searchResult.append("   💰 Giá: ")
                                   .append(formatPrice(product.get("price"))).append("\n");
                        searchResult.append("   📊 Tồn kho: ")
                                   .append(product.get("stock_quantity")).append(" sản phẩm\n");

                        if (product.get("description") != null) {
                            String desc = product.get("description").toString();
                            if (desc.length() > 80) {
                                desc = desc.substring(0, 80) + "...";
                            }
                            searchResult.append("   📝 Mô tả: ").append(desc).append("\n");
                        }
                        searchResult.append("\n");
                    }

                    return searchResult.toString();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Trích xuất từ khóa tìm kiếm từ câu hỏi
     */
    private String extractSearchTerm(String question) {
        String[] commonWords = {"có", "không", "bao", "nhiêu", "tìm", "kiếm",
                               "cho", "tôi", "biết", "trong", "hệ", "thống",
                               "của", "và", "là", "với", "được", "sản", "phẩm"};

        String[] words = question.split("\\s+");
        for (String word : words) {
            word = word.trim().toLowerCase()
                      .replaceAll("[?.,!]", "");

            if (word.length() >= 3) {
                boolean isCommon = false;
                for (String common : commonWords) {
                    if (word.equals(common)) {
                        isCommon = true;
                        break;
                    }
                }
                if (!isCommon) {
                    return word;
                }
            }
        }
        return null;
    }

    /**
     * Lấy thông tin về cấu trúc database
     */
    private String getDatabaseContext() {
        return """
                📋 CẤU TRÚC DATABASE:
                
                1. Bảng PRODUCTS (Sản phẩm):
                   - id, name, price, description, image, quantity, created_at
                
                2. Bảng CUSTOMERS (Khách hàng):
                   - id, name, email, phone, address, created_at
                
                3. Bảng ORDERS (Đơn hàng):
                   - id, customer_id, order_date, status, total_amount
                
                4. Bảng ORDERLINE (Chi tiết đơn hàng):
                   - id, order_id, product_id, quantity, unit_price
                
                5. Bảng USERS (Tài khoản):
                   - id, username, password, role (ADMIN/CUSTOMER)
                """;
    }

    /**
     * Lấy dữ liệu thống kê tổng quan từ database
     */
    private String getStatisticsData() {
        StringBuilder stats = new StringBuilder();
        stats.append("📊 THỐNG KÊ HỆ THỐNG:\n\n");

        try {
            // Đếm số lượng sản phẩm
            Long productCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM products", Long.class);
            stats.append("🛍️  Tổng số sản phẩm: ").append(productCount != null ? productCount : 0).append("\n");

            // Đếm số lượng khách hàng
            Long customerCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM customers", Long.class);
            stats.append("👥 Tổng số khách hàng: ").append(customerCount != null ? customerCount : 0).append("\n");

            // Đếm số lượng đơn hàng
            Long orderCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM orders", Long.class);
            stats.append("📦 Tổng số đơn hàng: ").append(orderCount != null ? orderCount : 0).append("\n\n");

            // Lấy top 5 sản phẩm
            List<Map<String, Object>> topProducts = jdbcTemplate.queryForList(
                    "SELECT name, price, stock_quantity FROM products ORDER BY id DESC LIMIT 5");

            if (!topProducts.isEmpty()) {
                stats.append("🔝 CÁC SẢN PHẨM GẦN ĐÂY:\n");
                int index = 1;
                for (Map<String, Object> product : topProducts) {
                    stats.append("  ").append(index++).append(". ")
                            .append(product.get("name"))
                            .append(" - Giá: ").append(formatPrice(product.get("price")))
                            .append(" - Tồn kho: ").append(product.get("stock_quantity")).append("\n");
                }
            }

            // Lấy thống kê đơn hàng gần đây
            List<Map<String, Object>> recentOrders = jdbcTemplate.queryForList(
                    "SELECT id, order_date, status, total_amount FROM orders ORDER BY order_date DESC LIMIT 3");

            if (!recentOrders.isEmpty()) {
                stats.append("\n📋 ĐƠN HÀNG GẦN ĐÂY:\n");
                for (Map<String, Object> order : recentOrders) {
                    stats.append("  • Đơn #").append(order.get("id"))
                            .append(" - Ngày: ").append(order.get("order_date"))
                            .append(" - Trạng thái: ").append(order.get("status"))
                            .append(" - Tổng: ").append(formatPrice(order.get("total_amount"))).append("\n");
                }
            }

        } catch (Exception e) {
            stats.append("❌ Không thể lấy dữ liệu thống kê: ").append(e.getMessage());
        }

        return stats.toString();
    }

    /**
     * Format giá tiền
     */
    private String formatPrice(Object price) {
        if (price == null) return "0đ";
        try {
            double priceValue = Double.parseDouble(price.toString());
            return String.format("%,.0fđ", priceValue);
        } catch (Exception e) {
            return price.toString() + "đ";
        }
    }

    /**
     * Truy vấn dữ liệu cụ thể từ database với bảo mật
     */
    public String queryDatabaseSafely(String tableName, String condition) {
        try {
            // Danh sách bảng được phép truy vấn
            List<String> allowedTables = List.of("products", "customers", "orders", "orderlines");

            if (!allowedTables.contains(tableName.toLowerCase())) {
                return "Bảng không được phép truy vấn";
            }

            String query = "SELECT * FROM " + tableName;
            if (condition != null && !condition.trim().isEmpty()) {
                query += " WHERE " + condition;
            }
            query += " LIMIT 10";

            List<Map<String, Object>> results = jdbcTemplate.queryForList(query);
            return formatQueryResults(results);
        } catch (Exception e) {
            return "Lỗi truy vấn: " + e.getMessage();
        }
    }

    /**
     * Format kết quả truy vấn thành chuỗi dễ đọc
     */
    private String formatQueryResults(List<Map<String, Object>> results) {
        if (results.isEmpty()) {
            return "Không tìm thấy kết quả";
        }

        StringBuilder formatted = new StringBuilder();
        formatted.append("Tìm thấy ").append(results.size()).append(" kết quả:\n\n");

        for (int i = 0; i < results.size(); i++) {
            formatted.append((i + 1)).append(". ").append(results.get(i).toString()).append("\n");
        }

        return formatted.toString();
    }
}
