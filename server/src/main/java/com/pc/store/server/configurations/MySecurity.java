package com.pc.store.server.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class MySecurity {
    private final String[] PUBLIC_ENDPOINTS = {
            "/api/customers/register", "/api/auth/log-in", "/api/auth/introspect", "/api/auth/logout", "/api/auth/refresh",
    };
    private final String[] PUBLIC_ENDPOINTS_GET = {
            "/api/products",
            "/api/products/asc",
            "/api/products/desc",
            "/api/products/{name}",
            "/api/product-detail/{id}",
            "/api/product-detail"
    };

    private  final String [] PUBLIC_ENDPOINTS_OPTIONS={
            "/api/customers/register", "/api/auth/log-in", "/api/auth/introspect", "/api/auth/logout", "/api/auth/refresh",
            "/api/products",
            "/api/products/asc",
            "/api/products/desc",
            "/api/products/{name}",
            "/api/product-detail/{id}",
            "/api/product-detail",
            "/api/customers/info"
    };

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authRes -> authRes.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS)
                .permitAll()
                .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS_GET)
                .permitAll()
                .anyRequest()
                .authenticated());
        httpSecurity.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.decoder(customJwtDecoder))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*"); // Frontend domain
        corsConfiguration.setAllowCredentials(true); // Cho phép cookie hoặc thông tin xác thực
        corsConfiguration.addAllowedMethod("*"); // Cho phép tất cả các phươngs thức HTTP
        corsConfiguration.addAllowedHeader("*"); // Cho phép tất cả các header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // Áp dụng cho tất cả các endpoint
        return source;
    }


}