package iuh.fit.truongthanhtung_22637091_shopping.service;

import iuh.fit.truongthanhtung_22637091_shopping.entity.User;
import iuh.fit.truongthanhtung_22637091_shopping.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Đăng ký user mới
     */
    @Transactional
    public User registerUser(String username, String password, String role) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        // Tạo user mới với mật khẩu đã mã hóa
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        return userRepository.save(user);
    }

    /**
     * Tìm user theo username
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Lấy tất cả users
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Kiểm tra username có tồn tại không
     */
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    /**
     * Cập nhật mật khẩu
     */
    @Transactional
    public void updatePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    /**
     * Cập nhật role
     */
    @Transactional
    public void updateRole(String username, String newRole) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setRole(newRole);
            userRepository.save(user);
        }
    }
}

