package iuh.fit.truongthanhtung_22637091_shopping.config;

import iuh.fit.truongthanhtung_22637091_shopping.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Cấu hình Spring Security cho hệ thống Shopping
 * 3 Role:
 * - GUEST: Xem danh sách sản phẩm, đăng ký tài khoản
 * - CUSTOMER: Xem sản phẩm, mua hàng, quản lý đơn hàng
 * - ADMIN: Quản trị toàn bộ hệ thống
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/ai/ask", "/ai/stats")
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. Quy tắc mở - GUEST có thể truy cập (không cần đăng nhập)
                        .requestMatchers("/", "/home", "/login", "/register",
                                "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()

                        // GUEST có thể xem danh sách Product và chi tiết
                        .requestMatchers("/product", "/product/search", "/product/detail/**").permitAll()

                        // GUEST có thể sử dụng AI Chat để tra cứu sản phẩm
                        .requestMatchers("/ai/**").permitAll()

                        // 2. Phân quyền CUSTOMER và ADMIN
                        // CUSTOMER: Xem sản phẩm, mua hàng, quản lý đơn hàng
                        .requestMatchers("/cart/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers("/order", "/order/my-orders", "/order/detail/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers("/orderline/detail/**").hasAnyRole("CUSTOMER", "ADMIN")

                        // 3. Độc quyền ADMIN - Quản trị Product
                        .requestMatchers("/product/new", "/product/edit/**",
                                "/product/update/**", "/product/delete/**").hasRole("ADMIN")

                        // Độc quyền ADMIN - Quản trị Customer
                        .requestMatchers("/customer/**").hasRole("ADMIN")

                        // Độc quyền ADMIN - Quản trị tất cả Order
                        .requestMatchers("/order/new", "/order/edit/**",
                                "/order/update/**", "/order/delete/**").hasRole("ADMIN")

                        // Độc quyền ADMIN - Quản trị OrderLine
                        .requestMatchers("/orderline/new", "/orderline/edit/**",
                                "/orderline/update/**", "/orderline/delete/**").hasRole("ADMIN")

                        // 4. Phần request còn lại phải chứng thực
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/product", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
