package iuh.fit.truongthanhtung_22637091_shopping.controller;

import iuh.fit.truongthanhtung_22637091_shopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Đăng xuất thành công!");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "success", required = false) String success,
                              Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", error);
        }
        if (success != null) {
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("confirmPassword") String confirmPassword,
                          @RequestParam(value = "role", defaultValue = "USER") String role) {
        try {
            // Validate
            if (username == null || username.trim().isEmpty()) {
                return "redirect:/register?error=Tên đăng nhập không được để trống!";
            }

            if (password == null || password.length() < 6) {
                return "redirect:/register?error=Mật khẩu phải có ít nhất 6 ký tự!";
            }

            if (!password.equals(confirmPassword)) {
                return "redirect:/register?error=Mật khẩu xác nhận không khớp!";
            }

            if (userService.existsByUsername(username)) {
                return "redirect:/register?error=Tên đăng nhập đã tồn tại!";
            }

            // Đăng ký user mới (mặc định role là USER)
            userService.registerUser(username, password, role);

            return "redirect:/register?success=true";

        } catch (Exception e) {
            return "redirect:/register?error=" + e.getMessage();
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
