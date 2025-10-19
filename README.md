# PC Store Project

## Introduction

PC Store is a web-based e-commerce application specializing in the sale of computers and computer components. This project is built using a microservices architecture, employing modern technologies to provide a seamless shopping experience for users.

## Technologies Used

### Backend

- **Java Spring Boot 3.3.5**: A robust framework for building Java-based web applications.
- **MongoDB**: A NoSQL database for storing product and user data.
- **Spring Security**: For securing the application and managing user authentication.
- **Maven**: A build automation tool for Java projects.
- **Lombok & Mapstruct**: For reducing boilerplate code and simplifying object mapping.

### Frontend

- **React 18.3**: A JavaScript library for building user interfaces.
- **TypeScript**: A typed superset of JavaScript for improved code quality.
- **Vite**: A fast development server and build tool for modern web applications.
- **TailwindCSS**: A utility-first CSS framework for creating responsive designs.
- **Shadcn/ui**: A component library for building UI elements.
- **Lucide Icons**: A set of open-source icons for use in the application.

## Key Features

- **Product Viewing**: Browse a list of products and view detailed information.
- **Search and Filter**: Easily search for products and apply filters to find the desired items.
- **Shopping Cart**: Add products to a shopping cart for convenient purchase.
- **Order Placement**: Simple order placement process for users.
- **User Account Management**: Manage user profiles and settings.
- **Order Management**: Admin capabilities for viewing and managing orders.
- **Admin Dashboard**: A dedicated interface for admins to oversee product listings and orders.

## Project Structure

### Backend (/server)

- **/src/main/java/com/pc/store/server/** - Main source code directory.
  - **/config/**: Contains Spring Boot configuration files.
  - **/controller/**: Contains RESTful controllers for handling API requests.
  - **/dto/**: Data Transfer Objects for transferring data between client and server.
  - **/exception/**: Exception handling classes.
  - **/mapper/**: MapStruct mappers for converting between entities and DTOs.
  - **/model/**: MongoDB entity classes representing the data structure.
  - **/repository/**: MongoDB repositories for data access.
  - **/security/**: Spring Security configurations for authentication and authorization.
  - **/service/**: Business logic implementations.
  - **/util/**: Utility classes for various functionalities.
- **/src/main/resources/**: Contains application configuration files and other resources.
- **/src/test/**: Unit tests for backend services.
- **pom.xml**: Maven configuration file for project dependencies.

### Frontend (/client)

- **/src/components/**: React components for building the UI.
- **/src/lib/**: Utility functions and helper methods.
- **/src/assets/**: Static assets like images and styles.
- **/src/hooks/**: Custom React hooks for reusable logic.

## Setup and Run Project

### System Requirements

- **Java 21**
- **Node.js 18+**
- **MongoDB**

### Backend Setup

1. **Clone the Repository**:

   ```bash
   git clone <repository-url>
   cd server
   ```

2. **Build the Project**:

   ```bash
   mvn clean install
   ```

3. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```

### Frontend Setup

1. **Navigate to the Client Directory**:

   ```bash
   cd client
   ```

2. **Install Dependencies**:

   ```bash
   npm install
   ```

3. **Run the Application**:
   ```bash
   npm run dev
   ```

## API Endpoints

### Products

| Method | Endpoint             | Description                              |
| ------ | -------------------- | ---------------------------------------- |
| GET    | `/api/products`      | Retrieve a list of all products.         |
| GET    | `/api/products/{id}` | Retrieve details of a specific product.  |
| POST   | `/api/products`      | Add a new product (Admin only).          |
| PUT    | `/api/products/{id}` | Update an existing product (Admin only). |
| DELETE | `/api/products/{id}` | Delete a product (Admin only).           |

### Orders

| Method | Endpoint           | Description                                 |
| ------ | ------------------ | ------------------------------------------- |
| GET    | `/api/orders`      | Retrieve a list of all orders.              |
| GET    | `/api/orders/{id}` | Retrieve details of a specific order.       |
| POST   | `/api/orders`      | Create a new order.                         |
| PUT    | `/api/orders/{id}` | Update the status of an order (Admin only). |

### Users

| Method | Endpoint             | Description                        |
| ------ | -------------------- | ---------------------------------- |
| POST   | `/api/auth/register` | Register a new user.               |
| POST   | `/api/auth/login`    | Log in an existing user.           |
| GET    | `/api/users/profile` | Retrieve user profile information. |
| PUT    | `/api/users/profile` | Update user profile information.   |

## Database Models

### Supplier

| Column Name | Data Type | Example                              | Constraints              |
| ----------- | --------- | ------------------------------------ | ------------------------ |
| id          | ObjectId  | ObjectId("605c72f9e1b5e6eaf5e8e1a1") | @Id                      |
| name        | String    | Dell                                 | @Size(max=255), @NotNull |
| address     | String    | Gò Vấp, Hồ Chí Minh                  | @Size(max=255), @NotNull |

### Product

| Column Name          | Data Type | Example                                                                                                                                                                                                    | Constraints              |
| -------------------- | --------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------ |
| id                   | ObjectId  | ObjectId("605c72f9e1b5e6eaf5e8e1a1")                                                                                                                                                                       | @Id, @GeneratedValue     |
| name                 | String    | "PC PV Home Office I70182 (Intel Core i7-12700/1 x 8GB/1TB SSD/Windows 11 Pro)"                                                                                                                            | @Size(max=255), @NotNull |
| img                  | String    | "[https://res.cloudinary.com/dubwmognz/image/upload/v1729782811/PC%20Store/pztejnub90jg2il2ofr0.webp](https://res.cloudinary.com/dubwmognz/image/upload/v1729782811/PC%20Store/pztejnub90jg2il2ofr0.webp)" | @URL                     |
| price_after_discount | Double    | 14690000                                                                                                                                                                                                   | @Positive, @NotNull      |
| original_price       | Double    | 16590000                                                                                                                                                                                                   | @Positive, @NotNull      |
| discount_percent     | Double    | 11.45                                                                                                                                                                                                      | @Positive, @NotNull      |
| price_discount       | Double    | 1900000                                                                                                                                                                                                    | @Positive, @NotNull      |

### ProductDetail

| Column Name      | Data Type | Example                              | Constraints              |
| ---------------- | --------- | ------------------------------------ | ------------------------ |
| product          | ObjectId  | ObjectId("605c72f9e1b5e6eaf5e8e1a1") | models.ObjectId(Product) |
| processor        | String    | "Intel Core i7-11700K"               | max_length=255           |
| ram              | String    | "16GB DDR4"                          | max_length=100           |
| storage          | String    | "1TB NVMe SSD"                       | max_length=255           |
| graphics_card    | String    | "NVIDIA GeForce RTX 3080"            | max_length=255           |
| power_supply     | String    | "750W 80+ Gold"                      | max_length=100           |
| motherboard      | String    | "ASUS ROG Strix Z590-E"              | max_length=255           |
| case             | String    | "NZXT H510"                          | max_length=255           |
| cooling_system   | String    | "Noctua NH-D15"                      | max_length=255           |
| operating_system | String    | "Windows 11 Pro"                     | max_length=100           |

### Customer

| Column Name  | Data Type | Example                              | Constraints                                                 |
| ------------ | --------- | ------------------------------------ | ----------------------------------------------------------- |
| id           | ObjectId  | ObjectId("605c72f9e1b5e6eaf5e8e1a1") | @Id, @GeneratedValue                                        |
| username     | String    | "johndoe"                            | @Size(max=150), @NotNull                                    |
| first_name   | String    | "John"                               | @Size(max=30)                                               |
| last_name    | String    | "Doe"                                | @Size(max=30)                                               |
| phone_number | String    | "0123456789"                         | @Size(max=10), @Pattern(regexp="^\\+?1?\\d{9,15}$", message |

="Phone number is not valid") |
| email | String | "johndoe@example.com" | @Email |
| password | String | "hashed_password" | @Size(min=8, max=255), @NotNull |

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more information.

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [React](https://reactjs.org/)
- [MongoDB](https://www.mongodb.com/)
- [TailwindCSS](https://tailwindcss.com/)
- [MapStruct](https://mapstruct.org/)
- [Lombok](https://projectlombok.org/)
- [Lucide Icons](https://lucide.dev/)

---
