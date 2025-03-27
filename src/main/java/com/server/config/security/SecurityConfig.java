package com.server.config.security;

import com.server.config.security.common.AuthAccessDeniedJwt;
import com.server.config.security.common.AuthEntryPointJwt;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final AuthEntryPointJwt authEntryPointJwt;
    private final AuthAccessDeniedJwt authAccessDeniedJwt;
    private final JwtUtils jwtUtils;

    private final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/sign-in",
            "/api/v1/auth/sign-up",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/verify-email",
            "/api/v1/auth/refresh-token",
            "/api/v1/auth/logout",
            "/api/v1/auth/activate",
            "/api/v1/game/**",
            "/api/v1/system-stream-link/view",
            "/api/v1/reels/view",
            "/api/v1/reels/view2",
            "/api/v1/reels/find-video-by-id/**",
            "/api/v1/tournament/view",
            "/api/v1/tournament/{id}",
            "/api/v1/tournament-match/find-by-tournament-id",
            "/api/v1/team-tournament-participations/find-by-tournament-id/**",
    };

    private final String[] SWAGGER_ENDPOINTS = {
            "/v2/api-docs",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };

    private final String[] WEBSOCKET_ENDPOINTS = {
            "/ws/**",
            "/api/our-websocket/**",
            "/chat/**"
    };

    private final String[] USER_ENDPOINTS = {
            "/api/v1/user/**",
            "/api/v1/auth/reset-password",
            "/api/v1/match/**",
            "/api/v1/team/**",
            "/api/v1/message/**",
            "/api/v1/user-team/**",
            "/api/v1/transactions",
            "/api/v1/transactions/generate-qr",
            "/api/v1/transactions/add-transaction",
            "/api/v1/transactions/search-by-id",
            "api/v1/notifications/**"
    };
    private final String[] ADMIN_ENDPOINTS = {
            "/api/v1/admin/**",
            "/api/v1/transactions/search",
            "/api/v1/room/delete-room",
            "/api/v1/transactions/update-transaction",
            "/api/v1/system-stream-link/update",
            "/api/v1/user/exportToExcel",
            "/api/v1/transactions/exportToExcelTransactions",
            "/api/v1/reels/add",
            "/api/v1/reels/add2"
    };

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(WEBSOCKET_ENDPOINTS).permitAll()
                        .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                        .requestMatchers("/api/v1/transactions/**").permitAll()
                        .requestMatchers(ADMIN_ENDPOINTS).hasAnyRole("ADMIN")
                        .anyRequest().authenticated());

        http.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(authEntryPointJwt)
                .accessDeniedHandler(authAccessDeniedJwt)
        );

        // Cấu hình logout
        http.logout(logout ->
                logout
                        .logoutUrl("/api/v1/auth/logout") // URL để logout
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("Logged out successfully!");
                        })
                        .deleteCookies("JSESSIONID") // Xóa cookie nếu sử dụng
                        .invalidateHttpSession(true)); // Hủy session

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://fptgamebooking.vn", "http://localhost:3000")); // Đảm bảo đúng domain
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
