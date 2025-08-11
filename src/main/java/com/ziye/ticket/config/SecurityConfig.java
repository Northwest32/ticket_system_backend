package com.ziye.ticket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.ziye.ticket.util.JwtUtil;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("🔍 Configuring SecurityFilterChain...");
        
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable()) 
            .addFilterBefore(jwtAuthenticationFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/register", "/api/login", "/api/logout","/api/me",
                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", 
                "/api/reset-password", 
                "/api/events/**",
                "/api/categories/**",
                "/api/users/**",
                "/api/comments/**",
                "/api/organizer-profile/**",
                "/api/upload-event-image",
                "/uploads/**",
                "/static/**",
                "/", "/index.html"
                ).permitAll()
                .anyRequest().authenticated()
            );
            
        System.out.println("🔍 SecurityFilterChain configured successfully");
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JWT认证过滤器
    public class JwtAuthenticationFilter extends OncePerRequestFilter {
        
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            
            System.out.println("Request: " + request.getMethod() + " " + request.getRequestURI());
            
            String authHeader = request.getHeader("Authorization");
            System.out.println("🔍 JWT Filter: Authorization header: " + authHeader);
            
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    Claims claims = jwtUtil.parseToken(token);
                    Long userId = Long.valueOf(claims.getSubject());
                    String userType = claims.get("userType", String.class);
                    String username = claims.get("username", String.class);
                    
                    System.out.println("🔍 JWT Filter: Token parsed successfully - userId: " + userId + ", userType: " + userType);
                    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userType.toUpperCase()))
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    System.out.println("🔍 JWT Filter: Authentication set successfully");
                    
                } catch (Exception e) {
                    System.out.println("❌ JWT Filter: Token parsing failed: " + e.getMessage());
                }
            } else {
                System.out.println("🔍 JWT Filter: No valid Authorization header found");
            }
            
            filterChain.doFilter(request, response);
            System.out.println(" Response: " + response.getStatus());
        }
    }
}
