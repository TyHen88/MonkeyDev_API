package com.dev.monkey_dev.config;

import com.dev.monkey_dev.properties.RsaKeyProperties;
import com.dev.monkey_dev.service.impl.UserAuthServiceImpl;
import com.dev.monkey_dev.service.impl.SpringOAuth2UserService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
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
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Security configuration for the application using JWT authentication with RSA
 * keys.
 * This configuration is conditionally enabled when RSA private key is present.
 */

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
        private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
        private final OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler;
        private final SpringOAuth2UserService springOAuth2UserService;

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
                                                .requestMatchers(
                                                                "/",
                                                                "/chat",
                                                                "/auth/**",
                                                                "/api/wb/v1/auth/**",
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
                                                                "/api/v1/ai-assistant/intents",
                                                                "/api/v1/ai-assistant/health",
                                                                "/api/v1/ai-assistant/test",
                                                                "/api/v1/ai-assistant/context",
                                                                "/api/v1/ai-assistant/process",
                                                                "/test/**")
                                                .permitAll()
                                                .requestMatchers(
                                                                "/api/wb/v1/auth/setup-password",
                                                                "/api/wb/v1/auth/update-password",
                                                                "/api/wb/v1/users/**",
                                                                "/api/wb/v1/trips/**",
                                                                "/api/wb/v1/files/upload-image",
                                                                "/api/wb/v1/calendar/**",
                                                                "/api/wb/v1/my-notes/**",
                                                                "/api/wb/v1/telegram/**",
                                                                "/api/wb/v1/chat/**",
                                                                "/api/v1/chat/**",
                                                                "/api/v1/conversations/**",
                                                                "/api/v1/message/**",
                                                                "/api/v1/contacts/**",
                                                                "/api/wb/v1/reminders/**",
                                                                "/api/v1/ai-assistant/process",
                                                                "/api/v1/ai-assistant/context")
                                                .authenticated()
                                                .requestMatchers("/ws/**").permitAll()
                                                .anyRequest().authenticated())
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .accessDeniedHandler(accessDeniedHandler)
                                                .authenticationEntryPoint(unauthorizedHandler))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                                .oauth2Login(oauth2 -> oauth2
                                                .successHandler(oauth2AuthenticationSuccessHandler)
                                                .failureHandler(oauth2AuthenticationFailureHandler)
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(springOAuth2UserService)))
                                // Temporarily disable OAuth2 resource server to test OAuth2 login
                                // .oauth2ResourceServer(oauth2 -> oauth2
                                // .authenticationEntryPoint(unauthorizedHandler)
                                // .accessDeniedHandler(accessDeniedHandler)
                                // .jwt(jwt -> jwt
                                // .jwtAuthenticationConverter(
                                // customJwtAuthenticationConverter))
                                // .opaqueToken(token -> token.disable()))
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