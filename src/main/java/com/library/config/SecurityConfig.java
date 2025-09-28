package com.library.config;

import com.library.security.JwtAuthenticationEntryPoint;
import com.library.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    
    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService,
                         JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                         JwtRequestFilter jwtRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    

    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/login", "/api/register").permitAll()  // Add these common paths
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/api/books/search").permitAll()
            .requestMatchers("/actuator/health").permitAll()  // Add health check
            .requestMatchers("/error").permitAll()  // Add error page
            .requestMatchers("/").permitAll()  // Add root path
            .requestMatchers("/api/books").hasAnyRole("LIBRARIAN", "PATRON")
            .requestMatchers("/api/books/**").hasRole("LIBRARIAN")
            .requestMatchers("/api/patrons/**").hasRole("LIBRARIAN")
            .requestMatchers("/api/borrow/**").hasAnyRole("LIBRARIAN", "PATRON")
            .requestMatchers("/api/authors/**").hasRole("LIBRARIAN")
            .requestMatchers("/api/categories/**").hasRole("LIBRARIAN")
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .headers(headers -> headers.frameOptions().disable()); // For H2 console
    
    http.authenticationProvider(authenticationProvider());
    http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
}   