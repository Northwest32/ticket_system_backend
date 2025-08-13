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
import org.springframework.http.HttpMethod;
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
                .requestMatchers("/api/register", "/api/login", "/api/logout",
                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", 
                "/api/reset-password", 
                "/uploads/**",
                "/static/**",
                "/", "/index.html"
                ).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/organizers/*/comments").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/events/*/comments").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/comments/*/replies").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/organizer-profile/*").permitAll()
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
            
            String requestURI = request.getRequestURI();
            String method = request.getMethod();
            System.out.println("Request: " + method + " " + requestURI);
            
            // Skip JWT processing for public endpoints
            if (isPublicEndpoint(requestURI, method)) {
                System.out.println("🔍 JWT Filter: Public endpoint, skipping authentication");
                filterChain.doFilter(request, response);
                System.out.println(" Response: " + response.getStatus());
                return;
            }
            
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
        
        private boolean isPublicEndpoint(String requestURI, String method) {
            // Always public endpoints (any method)
            if (requestURI.startsWith("/api/register") ||
                requestURI.startsWith("/api/login") ||
                requestURI.startsWith("/api/logout") ||
                requestURI.startsWith("/v3/api-docs/") ||
                requestURI.startsWith("/swagger-ui/") ||
                requestURI.startsWith("/swagger-ui.html") ||
                requestURI.startsWith("/api/reset-password") ||
                requestURI.startsWith("/uploads/") ||
                requestURI.startsWith("/static/") ||
                requestURI.equals("/") ||
                requestURI.equals("/index.html")) {
                return true;
            }
            
            // GET-only public endpoints
            if ("GET".equals(method)) {
                return requestURI.startsWith("/api/events/") ||
                       requestURI.startsWith("/api/categories/") ||
                       requestURI.matches("/api/organizers/\\d+/comments") ||
                       requestURI.matches("/api/events/\\d+/comments") ||
                       requestURI.matches("/api/comments/\\d+/replies") ||
                       requestURI.matches("/api/users/\\d+") ||
                       requestURI.matches("/api/organizer-profile/\\d+");
            }
            
            return false;
        }
    }
}
