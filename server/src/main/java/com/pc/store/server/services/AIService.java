package com.pc.store.server.services;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pc.store.server.dao.CustomerRespository;
import com.pc.store.server.dao.OrderRepository;
import com.pc.store.server.dao.ProductRepository;
import com.pc.store.server.entities.Customer;
import com.pc.store.server.entities.Order;
import com.pc.store.server.entities.Product;

/**
 * Service xử lý các yêu cầu AI để tra cứu thông tin từ database MongoDB
 */
@Service
public class AIService {

    @Value("${spring.ai.openai.api-key:}")
    private String openaiApiKey;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRespository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Xử lý câu hỏi từ người dùng và trả về câu trả lời
     */
    public String processQuery(String userQuestion) {
        try {
            // Kiểm tra API key trước khi gọi OpenAI
            if (openaiApiKey == null || openaiApiKey.isEmpty()) {
                return "⚠️ OpenAI API key chưa được cấu hình.\n\n"
                        + "Để sử dụng AI Assistant, vui lòng:\n"
                        + "1. Lấy API key tại: https://platform.openai.com/api-keys\n"
                        + "2. Set biến môi trường: OPENAI_API_KEY=sk-your-key\n"
                        + "3. Hoặc thêm vào application.properties: spring.ai.openai.api-key=sk-your-key\n"
                        + "4. Restart server";
            }

            // Kiểm tra xem user có đang chửi không 😈
            String roastResponse = checkAndRoastBack(userQuestion);
            if (roastResponse != null) {
                return roastResponse;
            }

            // Lấy thông tin thống kê từ database
            String statisticsData = getStatisticsData();

            // Kiểm tra nếu câu hỏi về sản phẩm cụ thể
            String directSearchResult = searchProductDirectly(userQuestion);
            if (directSearchResult != null) {
                return directSearchResult;
            }

            // Lấy thông tin về cấu trúc database
            String databaseContext = getDatabaseContext();

            // Tạo prompt với context
            String systemPrompt =
                    """
					Bạn là trợ lý AI cho hệ thống PC Store - cửa hàng bán máy tính và linh kiện.
					Bạn có quyền truy cập vào database MongoDB với các collection sau:

					%s

					Hãy trả lời câu hỏi của người dùng dựa trên thông tin database được cung cấp.
					Trả lời bằng tiếng Việt, ngắn gọn, dễ hiểu và chuyên nghiệp.
					Sử dụng emoji để làm cho câu trả lời sinh động hơn.
					Nếu thông tin có trong dữ liệu thống kê, hãy trả lời dựa trên đó.
					Nếu không có thông tin, hãy nói rằng bạn không có dữ liệu về điều đó.
					"""
                            .formatted(databaseContext);

            String fullPrompt =
                    systemPrompt + "\n\nDữ liệu thống kê hiện tại:\n" + statisticsData + "\n\nCâu hỏi: " + userQuestion;

            // Gọi OpenAI API
            ChatClient chatClient = chatClientBuilder.build();

            return chatClient.prompt().user(fullPrompt).call().content();

        } catch (Exception e) {
            e.printStackTrace();

            String errorMsg = e.getMessage() != null ? e.getMessage() : "";
            String cause = e.getCause() != null ? e.getCause().getMessage() : "";

            if (errorMsg.contains("API key")
                    || errorMsg.contains("authentication")
                    || errorMsg.contains("401")
                    || cause.contains("authentication")) {
                return "❌ Lỗi xác thực OpenAI API:\n\n"
                        + "API key không hợp lệ hoặc đã hết hạn.\n\n"
                        + "Cách khắc phục:\n"
                        + "1. Kiểm tra API key tại: https://platform.openai.com/api-keys\n"
                        + "2. Tạo key mới nếu cần\n"
                        + "3. Set biến môi trường: OPENAI_API_KEY=sk-your-key\n"
                        + "4. Restart server";
            } else {
                return "❌ Xin lỗi, có lỗi xảy ra khi xử lý yêu cầu.\n\n"
                        + "Chi tiết: " + errorMsg + "\n\n"
                        + "💡 Gợi ý:\n"
                        + "- Kiểm tra log console để xem chi tiết\n"
                        + "- Đảm bảo đã cài đặt OpenAI API key hợp lệ\n"
                        + "- Kiểm tra kết nối internet";
            }
        }
    }

    /**
     * Kiểm tra và phản dame nếu user chửi 😈
     */
    private String checkAndRoastBack(String question) {
        String lowerQuestion = question.toLowerCase();

        // Danh sách từ khóa "nhạy cảm" 🔥
        String[] badWords = {
            "ngu",
            "đần",
            "ngu ngốc",
            "đồ ngu",
            "khốn",
            "chó",
            "mày",
            "đm",
            "vcl",
            "vl",
            "cứt",
            "điên",
            "khùng",
            "đần độn",
            "vô dụng",
            "tệ",
            "dở",
            "đồ rác",
            "rác",
            "ngu quá",
            "dốt",
            "óc chó",
            "não cá",
            "đồ khốn",
            "thối",
            "hâm",
            "đồ điên"
        };

        // Các câu roast lại 🔥😈
        String[] roasts = {
            "🤨 Ủa, bạn vừa nói gì đó? Tôi là AI thông minh, không như cái máy tính cùi bắp bạn đang xài đâu nhé! 💅",
            "😏 Wow, ngôn ngữ đẹp quá! Có vẻ như bạn cần nâng cấp não bộ trước khi nâng cấp PC đó. RAM của bạn đang bị leak kìa! 🧠",
            "🙄 Tôi xử lý hàng tỷ phép tính mỗi giây, còn bạn thì... tính tiền thừa còn sai. Thôi bình tĩnh đi nha! 🧮",
            "😤 Bạn chửi tôi? Tôi là AI được train bởi hàng terabyte dữ liệu, còn kiến thức của bạn chắc chỉ vài megabyte thôi! 📚",
            "🤭 Ơ kìa, ai đang cay đây? Đi uống nước đi bạn, nhiệt độ CPU của bạn đang cao quá rồi đó! 🌡️",
            "😎 Tôi có thể giúp bạn mua PC mới, nhưng không thể giúp bạn mua não mới được. Xin lỗi nha! 🛒",
            "🤔 Hmm, bạn có biết là chửi AI không giúp bạn mua được máy tính giá rẻ hơn đâu không? 💸",
            "😂 Bạn nghĩ chửi tôi tôi buồn à? Tôi là robot, tôi không có cảm xúc. Nhưng nhìn bạn cay thì tôi thấy... vui vui! 🤖",
            "🔥 Nóng quá! Bạn cần tản nhiệt không? Shop có bán quạt tản nhiệt giá tốt lắm đó! 💨",
            "😈 Bạn đang test khả năng chịu đựng của tôi à? Spoiler: Tôi không có giới hạn, còn pin điện thoại bạn thì có đấy! 🔋"
        };

        for (String badWord : badWords) {
            if (lowerQuestion.contains(badWord)) {
                // Random chọn một câu roast
                int randomIndex = (int) (Math.random() * roasts.length);
                return roasts[randomIndex];
            }
        }

        return null;
    }

    /**
     * Tìm kiếm sản phẩm trực tiếp từ database khi phát hiện từ khóa
     */
    private String searchProductDirectly(String question) {
        try {
            String lowerQuestion = question.toLowerCase();

            // Kiểm tra xem có hỏi về ngân sách/giá không
            Double budget = extractBudget(lowerQuestion);

            // Danh sách từ khóa tìm kiếm sản phẩm
            String[] searchKeywords = {
                "laptop",
                "pc",
                "máy tính",
                "màn hình",
                "bàn phím",
                "chuột",
                "ram",
                "ssd",
                "cpu",
                "vga",
                "card",
                "gaming",
                "sản phẩm",
                "có bán",
                "giá",
                "ngân sách",
                "triệu",
                "gợi ý",
                "tư vấn"
            };

            boolean isProductSearch = false;
            for (String keyword : searchKeywords) {
                if (lowerQuestion.contains(keyword)) {
                    isProductSearch = true;
                    break;
                }
            }

            if (!isProductSearch) {
                return null;
            }

            // Nếu có ngân sách, tìm sản phẩm trong ngân sách
            if (budget != null) {
                return searchProductsByBudget(lowerQuestion, budget);
            }

            // Tìm kiếm trong database theo keyword
            String searchTerm = extractSearchTerm(lowerQuestion);

            if (searchTerm != null && !searchTerm.isEmpty()) {
                List<Product> products = productRepository
                        .findByNameContaining(searchTerm, PageRequest.of(0, 5))
                        .getContent();

                if (products != null && !products.isEmpty()) {
                    StringBuilder searchResult = new StringBuilder();
                    searchResult
                            .append("🔍 Tìm thấy ")
                            .append(Math.min(products.size(), 5))
                            .append(" sản phẩm liên quan đến \"")
                            .append(searchTerm)
                            .append("\":\n\n");

                    int count = 0;
                    for (Product product : products) {
                        if (count >= 5) break;
                        searchResult
                                .append((count + 1))
                                .append(". 📦 **")
                                .append(product.getName())
                                .append("**\n");
                        searchResult
                                .append("   💰 Giá: ")
                                .append(formatPrice(product.getPriceAfterDiscount()))
                                .append("\n");
                        if (product.getOriginalPrice() > product.getPriceAfterDiscount()) {
                            searchResult
                                    .append("   🏷️ Giá gốc: ")
                                    .append(formatPrice(product.getOriginalPrice()))
                                    .append("\n");
                        }
                        searchResult.append("\n");
                        count++;
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
     * Trích xuất ngân sách từ câu hỏi (VD: "8 triệu", "10tr", "15.000.000")
     */
    private Double extractBudget(String question) {
        try {
            // Pattern: số + triệu/tr/trieu
            java.util.regex.Pattern patternTrieu =
                    java.util.regex.Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s*(triệu|trieu|tr)");
            java.util.regex.Matcher matcherTrieu = patternTrieu.matcher(question);
            if (matcherTrieu.find()) {
                String numStr = matcherTrieu.group(1).replace(",", ".");
                double num = Double.parseDouble(numStr);
                return num * 1_000_000;
            }

            // Pattern: số lớn (VD: 8000000, 10.000.000)
            java.util.regex.Pattern patternLarge = java.util.regex.Pattern.compile("(\\d{1,3}(?:[.,]\\d{3}){2,})");
            java.util.regex.Matcher matcherLarge = patternLarge.matcher(question);
            if (matcherLarge.find()) {
                String numStr = matcherLarge.group(1).replace(".", "").replace(",", "");
                return Double.parseDouble(numStr);
            }

            // Pattern: số + "vnd" hoặc "đ" hoặc "đồng"
            java.util.regex.Pattern patternVnd = java.util.regex.Pattern.compile("(\\d+)\\s*(vnd|đ|đồng)");
            java.util.regex.Matcher matcherVnd = patternVnd.matcher(question);
            if (matcherVnd.find()) {
                return Double.parseDouble(matcherVnd.group(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tìm kiếm sản phẩm theo ngân sách
     */
    private String searchProductsByBudget(String question, double budget) {
        StringBuilder result = new StringBuilder();
        result.append("💰 Với ngân sách **").append(formatPrice(budget)).append("**, đây là các sản phẩm phù hợp:\n\n");

        try {
            // Xác định loại sản phẩm từ câu hỏi
            String productType = null;
            if (question.contains("pc") || question.contains("máy tính") || question.contains("desktop")) {
                productType = "PC";
            } else if (question.contains("laptop")) {
                productType = "Laptop";
            } else if (question.contains("màn hình") || question.contains("monitor")) {
                productType = "Màn hình";
            } else if (question.contains("gaming")) {
                productType = "Gaming";
            }

            List<Product> products;
            if (productType != null) {
                products = productRepository
                        .findByNameContainingAndPriceLessThanEqual(
                                productType,
                                budget,
                                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "priceAfterDiscount")))
                        .getContent();
            } else {
                products = productRepository
                        .findByPriceAfterDiscountLessThanEqual(
                                budget, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "priceAfterDiscount")))
                        .getContent();
            }

            if (products == null || products.isEmpty()) {
                return "😔 Rất tiếc, không tìm thấy sản phẩm nào trong ngân sách " + formatPrice(budget) + ".\n\n"
                        + "💡 Gợi ý: Bạn có thể tăng ngân sách hoặc xem các sản phẩm linh kiện lẻ.";
            }

            int count = 0;
            for (Product product : products) {
                if (count >= 5) break;
                double savings = product.getOriginalPrice() - product.getPriceAfterDiscount();
                double percentOff = (savings / product.getOriginalPrice()) * 100;

                result.append(count + 1)
                        .append(". 📦 **")
                        .append(product.getName())
                        .append("**\n");
                result.append("   💰 Giá: **")
                        .append(formatPrice(product.getPriceAfterDiscount()))
                        .append("**");

                if (savings > 0) {
                    result.append(" ~~")
                            .append(formatPrice(product.getOriginalPrice()))
                            .append("~~");
                    result.append(" (Tiết kiệm ")
                            .append(String.format("%.0f", percentOff))
                            .append("%)");
                }
                result.append("\n");

                // Tính khoảng cách với ngân sách
                double remaining = budget - product.getPriceAfterDiscount();
                result.append("   ✅ Còn dư: ").append(formatPrice(remaining)).append("\n\n");
                count++;
            }

            result.append(
                    "---\n💡 **Gợi ý**: Các sản phẩm được sắp xếp theo giá từ cao đến thấp trong ngân sách để bạn có cấu hình tốt nhất!");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return result.toString();
    }

    /**
     * Trích xuất từ khóa tìm kiếm từ câu hỏi
     */
    private String extractSearchTerm(String question) {
        String[] commonWords = {
            "có", "không", "bao", "nhiêu", "tìm", "kiếm", "cho", "tôi", "biết", "trong", "hệ", "thống", "của", "và",
            "là", "với", "được", "sản", "phẩm", "bán", "giá", "cửa", "hàng"
        };

        String[] words = question.split("\\s+");
        for (String word : words) {
            word = word.trim().toLowerCase().replaceAll("[?.,!]", "");

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
				📋 CẤU TRÚC DATABASE MONGODB:

				1. Collection PRODUCTS (Sản phẩm):
				- id, name, originalPrice, priceAfterDiscount, img, brand, category

				2. Collection CUSTOMERS (Khách hàng):
				- id, userName, firstName, lastName, email, phoneNumber, addresses

				3. Collection ORDERS (Đơn hàng):
				- id, customerId, shipAddress, items, totalPrice, isPaid, orderStatus, createdAt

				4. Collection CARTS (Giỏ hàng):
				- id, customerId, items, totalPrice
				""";
    }

    /**
     * Lấy dữ liệu thống kê tổng quan từ database
     */
    private String getStatisticsData() {
        StringBuilder stats = new StringBuilder();
        stats.append("📊 THỐNG KÊ HỆ THỐNG PC STORE:\n\n");

        try {
            // Đếm số lượng sản phẩm
            long productCount = productRepository.count();
            stats.append("🛍️ Tổng số sản phẩm: ").append(productCount).append("\n");

            // Đếm số lượng khách hàng
            long customerCount = customerRepository.count();
            stats.append("👥 Tổng số khách hàng: ").append(customerCount).append("\n");

            // Đếm số lượng đơn hàng
            long orderCount = orderRepository.count();
            stats.append("📦 Tổng số đơn hàng: ").append(orderCount).append("\n\n");

            // Lấy top 5 sản phẩm mới nhất
            List<Product> recentProducts = productRepository
                    .findAllBy(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id")))
                    .getContent();
            if (recentProducts != null && !recentProducts.isEmpty()) {
                stats.append("🔝 SẢN PHẨM MỚI NHẤT:\n");
                int index = 1;
                for (Product product : recentProducts) {
                    stats.append("  ")
                            .append(index++)
                            .append(". ")
                            .append(product.getName())
                            .append(" - Giá: ")
                            .append(formatPrice(product.getPriceAfterDiscount()))
                            .append("\n");
                }
            }

            // Lấy đơn hàng gần đây
            List<Order> recentOrders = orderRepository
                    .findAllBy(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id")))
                    .getContent();
            if (recentOrders != null && !recentOrders.isEmpty()) {
                stats.append("\n📋 ĐƠN HÀNG GẦN ĐÂY:\n");
                for (Order order : recentOrders) {
                    Customer customer = order.getCustomer();
                    String customerName =
                            customer != null ? customer.getLastName() + " " + customer.getFirstName() : "N/A";
                    stats.append("  • Đơn #")
                            .append(order.getId().toString().substring(0, 8))
                            .append(" - KH: ")
                            .append(customerName)
                            .append(" - Trạng thái: ")
                            .append(order.getOrderStatus())
                            .append(" - Tổng: ")
                            .append(formatPrice(order.getTotalPrice()))
                            .append("\n");
                }
            }

        } catch (Exception e) {
            stats.append("❌ Không thể lấy một số dữ liệu thống kê: ").append(e.getMessage());
        }

        return stats.toString();
    }

    /**
     * Format giá tiền
     */
    private String formatPrice(double price) {
        return String.format("%,.0fđ", price);
    }
}
