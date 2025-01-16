package com.dailycodework.dreamshops.security.config;

import com.dailycodework.dreamshops.security.service.AuthFilterService;
import com.dailycodework.dreamshops.security.service.JwtAuthEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final AuthFilterService authFilterService;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthEntryPoint jwtAuthEntryPoint ;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**", "/forgotPassword/**").permitAll()// public endpoints.permitAll()
                        .requestMatchers("/api/v1/addresses/**").authenticated() //  Address endpoints require authentication
                        .requestMatchers("/api/v1/products/all").permitAll()
                        .requestMatchers("/api/v1/products/product/**").permitAll()
                        .requestMatchers("/api/v1/products/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/products/product").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/{userId}").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/orders/order/{orderId}").hasRole("USER")

                        .requestMatchers(HttpMethod.GET, "/api/v1/carts/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/carts/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/carts/{cartId}/total-price").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/cart-items/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cart-items/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/cart-items/**").hasRole("USER")

                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/name/{name}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/categories/category").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/categories/{id}").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/images/image/download/{imageId}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/images/images").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/images/image/{imageId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/images/image/{imageId}").hasRole("ADMIN")
                        .anyRequest().authenticated()) // all other endpoints authenticated
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthEntryPoint))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(authFilterService, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("https://app-backend.com", "http://localhost:8080")); //TODO: update backend url
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
//        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
}
