package com.grepp.spring.infra.config.security;

import com.grepp.spring.infra.auth.jwt.JwtAuthenticationEntryPoint;
import com.grepp.spring.infra.auth.jwt.filter.JwtAuthenticationFilter;
import com.grepp.spring.infra.auth.jwt.filter.JwtExceptionFilter;
import com.grepp.spring.app.model.auth.service.CustomOAuth2UserService;
import com.grepp.spring.app.model.auth.service.CustomOAuth2SuccessHandler;
import com.grepp.spring.app.model.auth.service.CustomOAuth2FailureHandler;
import com.grepp.spring.infra.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Profile("!mock")
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
    private final CorsConfig corsConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        (requests) -> requests
                                .requestMatchers("/favicon.ico", "/img/**", "/js/**", "/css/**").permitAll()
                                .requestMatchers("/", "/error", "/auth/login", "/auth/signup").permitAll()
                                .requestMatchers("/login/oauth2/code/**").permitAll()
                                .requestMatchers("/oauth2/authorization/**").permitAll()
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/oauth-signup").permitAll()
                                .requestMatchers("/api/members/login", "/api/members/signup", "/api/members/email/**").permitAll()
                                .requestMatchers("/api/members/oauth-signup").permitAll()
                                .requestMatchers("/api/members/email/find", "/api/members/password/find").permitAll()
                                .requestMatchers("/api/members/nickname/check").permitAll()
                                .requestMatchers("/api/users/top-savers", "/api/users/top-challenges", "/api/users/average-saving", "/api/users/all-saving").permitAll()
                                .requestMatchers("/api/searches/top","/api/places/search","/api/places/detail").permitAll()
                                .requestMatchers("/api/members/logout").authenticated()
                                .requestMatchers("/api/**").authenticated()
                                .anyRequest().permitAll()
                )
                // jwtAuthenticationEntryPoint 는 oauth 인증을 사용할 경우 제거
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService)
                    )
                    .successHandler(customOAuth2SuccessHandler)
                    .failureHandler(customOAuth2FailureHandler)
                )
        ;
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return corsConfig.corsConfigurationSource();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                            .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public OAuth2UserService<org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new DefaultOAuth2UserService();
    }
}
