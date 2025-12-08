package iuh.fit.truongthanhtung_22637091_shopping.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class để generate BCrypt password hash
 * Chạy class này để lấy mật khẩu đã mã hóa cho database
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("=== BCrypt Password Generator ===");
        System.out.println();

        // Generate password cho admin
        String adminPassword = "admin123";
        String adminHash = encoder.encode(adminPassword);
        System.out.println("Username: admin");
        System.out.println("Password: " + adminPassword);
        System.out.println("Hash: " + adminHash);
        System.out.println();

        // Generate password cho user
        String userPassword = "user123";
        String userHash = encoder.encode(userPassword);
        System.out.println("Username: user");
        System.out.println("Password: " + userPassword);
        System.out.println("Hash: " + userHash);
        System.out.println();

        // SQL Insert statements
        System.out.println("=== SQL INSERT Statements ===");
        System.out.println("INSERT INTO users (username, password, role) VALUES");
        System.out.println("('admin', '" + adminHash + "', 'ADMIN'),");
        System.out.println("('user', '" + userHash + "', 'USER');");
    }
}

