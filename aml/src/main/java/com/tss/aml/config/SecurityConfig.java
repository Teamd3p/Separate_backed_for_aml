package com.tss.aml.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tss.aml.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/verify-otp", "/api/auth/resend-otp", "/api/auth/forgot-password", "/api/auth/reset-password").permitAll()
                
                // Admin only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Compliance officer endpoints
                .requestMatchers("/api/compliance/**").hasAnyRole("ADMIN", "COMPLIANCE_OFFICER")
                .requestMatchers("/api/kyc/compliance/**").hasAnyRole("ADMIN", "COMPLIANCE_OFFICER")
                
                // Customer endpoints - customers can only access their own data
                .requestMatchers("/api/customers/**").hasRole("CUSTOMER")
                
                // Transaction endpoints - role-based access
                .requestMatchers("/api/transactions/**").hasAnyRole("CUSTOMER", "ADMIN", "COMPLIANCE_OFFICER")
                
                // Account endpoints - role-based access
                .requestMatchers("/api/accounts/**").hasAnyRole("CUSTOMER", "ADMIN")
                
                // KYC endpoints - authenticated users
                .requestMatchers("/api/kyc/**").authenticated()
                
                // All other API endpoints require authentication
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
