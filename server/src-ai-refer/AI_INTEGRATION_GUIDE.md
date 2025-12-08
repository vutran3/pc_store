# Hướng dẫn tích hợp AI vào dự án Shopping

## 📋 Tổng quan
Tính năng AI Assistant đã được tích hợp vào hệ thống Shopping, cho phép CUSTOMER và ADMIN tra cứu thông tin từ database MariaDB thông qua giao diện chat thân thiện.

## 🚀 Các bước cài đặt

### 1. Cài đặt dependencies
File `pom.xml` đã được cập nhật với:
- Spring AI OpenAI Starter
- Jackson Databind (xử lý JSON)

### 2. Cấu hình OpenAI API Key

**Cách 1: Thông qua biến môi trường (Khuyến nghị)**
```bash
# Windows CMD
set OPENAI_API_KEY=sk-your-api-key-here

# Windows PowerShell
$env:OPENAI_API_KEY="sk-your-api-key-here"
```

**Cách 2: Cập nhật trực tiếp trong application.properties**
```properties
spring.ai.openai.api-key=sk-your-api-key-here
```

### 3. Lấy OpenAI API Key
1. Truy cập: https://platform.openai.com/
2. Đăng ký/Đăng nhập
3. Vào **API Keys** section
4. Tạo key mới: **Create new secret key**
5. Copy key và lưu lại (chỉ hiển thị 1 lần)

### 4. Build và chạy ứng dụng
```bash
# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

## 📁 Các file đã được tạo/cập nhật

### 1. AIService.java
- **Đường dẫn**: `src/main/java/.../service/AIService.java`
- **Chức năng**: Xử lý logic AI, kết nối với OpenAI, truy vấn database

### 2. AIController.java
- **Đường dẫn**: `src/main/java/.../controller/AIController.java`
- **Endpoints**:
  - `GET /ai/chat` - Hiển thị giao diện chat
  - `POST /ai/ask` - API để gửi câu hỏi
  - `GET /ai/stats` - Lấy thống kê nhanh

### 3. chat.html
- **Đường dẫn**: `src/main/resources/templates/ai/chat.html`
- **Chức năng**: Giao diện chat AI với thiết kế hiện đại

### 4. SecurityConfig.java
- **Cập nhật**: Thêm quyền truy cập `/ai/**` cho CUSTOMER và ADMIN

### 5. application.properties
- **Cập nhật**: Thêm cấu hình OpenAI API

## 🎯 Cách sử dụng

### 1. Truy cập AI Assistant
- **URL**: http://localhost:8080/ai/chat
- **Yêu cầu**: Phải đăng nhập với role CUSTOMER hoặc ADMIN

### 2. Các câu hỏi mẫu
```
✅ "Có bao nhiêu sản phẩm trong hệ thống?"
✅ "Cho tôi biết về các đơn hàng gần đây"
✅ "Có bao nhiêu khách hàng?"
✅ "Cho tôi xem tổng quan về hệ thống"
✅ "Sản phẩm nào có giá cao nhất?"
✅ "Thống kê doanh thu hôm nay"
```

### 3. Tính năng của AI Assistant
- ✅ Tra cứu thông tin sản phẩm
- ✅ Thống kê đơn hàng
- ✅ Thông tin khách hàng
- ✅ Tổng quan hệ thống
- ✅ Trả lời bằng tiếng Việt
- ✅ Giao diện chat trực quan

## 🔧 Cấu trúc hoạt động

```
User → chat.html → AIController → AIService → [OpenAI API + MariaDB] → Response
```

1. **User**: Nhập câu hỏi trong giao diện chat
2. **chat.html**: Gửi request qua AJAX
3. **AIController**: Nhận request và gọi AIService
4. **AIService**: 
   - Lấy thông tin từ database
   - Gửi câu hỏi + context đến OpenAI
   - OpenAI phân tích và trả lời
5. **Response**: Hiển thị câu trả lời trong chat

## 📊 Database được hỗ trợ

AI Assistant có thể tra cứu các bảng:
- `product` - Sản phẩm
- `customer` - Khách hàng
- `orders` - Đơn hàng
- `order_line` - Chi tiết đơn hàng
- `user` - Tài khoản người dùng

## 🎨 Giao diện

Giao diện chat có các tính năng:
- 💬 Chat box hiện đại với gradient design
- 🎯 Suggested questions (câu hỏi gợi ý)
- ⚡ Real-time response
- 📱 Responsive design
- 🔄 Loading animation

## ⚠️ Lưu ý quan trọng

### 1. Chi phí OpenAI API
- Model sử dụng: **gpt-4o-mini** (giá rẻ, hiệu quả)
- Mỗi request có chi phí nhỏ
- Nên theo dõi usage tại: https://platform.openai.com/usage

### 2. Bảo mật API Key
- ❌ KHÔNG commit API key lên Git
- ✅ Sử dụng biến môi trường
- ✅ Thêm vào .gitignore nếu lưu trong file

### 3. Rate Limiting
- OpenAI có giới hạn số request/phút
- Nếu lỗi 429, đợi vài giây rồi thử lại

## 🔄 Nâng cấp trong tương lai

### Có thể mở rộng:
1. **Function Calling**: Cho phép AI gọi các function cụ thể
2. **Memory**: Lưu lịch sử chat
3. **Multi-language**: Hỗ trợ nhiều ngôn ngữ
4. **Voice Input**: Nhập câu hỏi bằng giọng nói
5. **Advanced Analytics**: Phân tích dữ liệu phức tạp hơn

## 🐛 Troubleshooting

### Lỗi: "API key not found"
```bash
# Kiểm tra biến môi trường
echo %OPENAI_API_KEY%  # Windows CMD
$env:OPENAI_API_KEY    # PowerShell

# Set lại nếu chưa có
set OPENAI_API_KEY=sk-your-key
```

### Lỗi: "Cannot connect to OpenAI"
- Kiểm tra kết nối internet
- Kiểm tra API key còn hiệu lực
- Kiểm tra quota tại OpenAI dashboard

### Lỗi: "Database connection failed"
- Kiểm tra MariaDB đang chạy (port 3388)
- Kiểm tra credentials trong application.properties
- Kiểm tra database `shoppingdata` đã tồn tại

## 📞 Hỗ trợ

Nếu gặp vấn đề, kiểm tra:
1. Console log trong browser (F12)
2. Spring Boot console log
3. File application.properties
4. OpenAI API dashboard

## ✨ Demo

Sau khi cài đặt xong:
1. Start ứng dụng
2. Đăng nhập với tài khoản CUSTOMER/ADMIN
3. Truy cập: http://localhost:8080/ai/chat
4. Thử các câu hỏi mẫu
5. Xem kết quả!

---
**Tạo bởi**: Trương Thành Tùng - 22637091
**Ngày**: November 2025

