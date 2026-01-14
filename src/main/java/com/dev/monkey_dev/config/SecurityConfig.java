package com.dev.monkey_dev.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.dev.monkey_dev.properties.RsaKeyProperties;
import com.dev.monkey_dev.service.users.UserAuthServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "rsa", name = "private-key")
public class SecurityConfig {
        private final RsaKeyProperties rsaKeys;
        private final UnauthorizedHandler unauthorizedHandler;
        private final AccessDeniedHandler accessDeniedHandler;
        private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;
        private final PasswordEncoder passwordEncoder;

        @Primary
        @Bean("userAuthProvider")
        public AuthenticationManager userAuthProvider(UserAuthServiceImpl userDetailsService) {
                var authProvider = new DaoAuthenticationProvider();
                authProvider.setPasswordEncoder(passwordEncoder);
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setHideUserNotFoundExceptions(false);
                return new ProviderManager(authProvider);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors
                                                .configurationSource(request -> {
                                                        CorsConfiguration config = new CorsConfiguration();
                                                        config.setAllowedOriginPatterns(List.of("*"));
                                                        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE",
                                                                        "OPTIONS"));
                                                        config.setAllowedHeaders(List.of("*"));
                                                        config.setAllowCredentials(true);
                                                        return config;
                                                }))
                                .authorizeHttpRequests(auth -> auth
                                                // Publicly accessible endpoints
                                                .requestMatchers(
                                                                "/",
                                                                "/chat",
                                                                "/auth/**",
                                                                "/api/wb/v1/auth/login",
                                                                "/api/wb/v1/auth/refresh",
                                                                "/api/wb/v1/admin/users/register",
                                                                "/api/wb/v1/auth/encrypt",
                                                                "/api/wb/v1/password/**",
                                                                "/api/v1/auth/**",
                                                                "/api/v1/image/**",
                                                                "/oauth2/**",
                                                                "/login/oauth2/**",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/v3/api-docs.yaml",
                                                                "/swagger-ui.html",
                                                                "/swagger-ui/index.html",
                                                                "/webjars/**",
                                                                "/swagger-resources/**",
                                                                "/swagger-ui.html/**",
                                                                "/swagger-ui.html**",
                                                                "/swagger.json",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui/index.html",
                                                                "/actuator/**",
                                                                "/test/**",
                                                                "/ws/**")
                                                .permitAll()
                                                // ADMIN role endpoints
                                                .requestMatchers(
                                                                "/api/wb/v1/admin/users/**")
                                                .hasRole("ADMIN")
                                                // authenticated user endpoints
                                                .requestMatchers("/api/wb/v1/user/create").hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/wb/v1/user/**",
                                                                "/api/wb/v1/auth/encrypt")
                                                .hasAnyRole("USER", "ADMIN")
                                                // All others require authentication
                                                .anyRequest().authenticated())
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .accessDeniedHandler(accessDeniedHandler)
                                                .authenticationEntryPoint(unauthorizedHandler))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .authenticationEntryPoint(unauthorizedHandler)
                                                .accessDeniedHandler(accessDeniedHandler)
                                                .jwt(jwt -> jwt
                                                                .jwtAuthenticationConverter(
                                                                                customJwtAuthenticationConverter)))
                                .build();
        }

        @Bean
        @Primary
        JwtDecoder jwtDecoder() {
                return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
        }

        @Bean
        @Primary
        JwtEncoder jwtEncoder() {
                JWK jwk = new RSAKey.Builder(rsaKeys.publicKey())
                                .privateKey(rsaKeys.privateKey())
                                .build();
                JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
                return new NimbusJwtEncoder(jwkSource);
        }
}