package com.ll.security250106.global.security;

import com.ll.security250106.global.rsData.RsData;
import com.ll.security250106.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAuthenticationFilter customAuthenticationFilter;

    @Bean
    SecurityFilterChain baseSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers("/h2-console/**")
                                        .permitAll()
//                                .requestMatchers("/h2-console/login.do")    // 상위 룰 우선이기 때문에 의미 없음
//                                .authenticated()                              // 상위 룰 우선이기 때문에 의미 없음
                                        .requestMatchers("/api/*/members/login",
                                                "/api/*/members/join")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET,
                                                "/api/*/posts",
                                                "/api/*/posts/{id:\\d+}",
                                                "/api/*/posts/{postId:\\d+}/comments")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated()
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(
                                (request, response, authException) -> {
                                    response.setContentType("application/json;charset=UTF-8");

                                    boolean is401 = authException.getLocalizedMessage()
                                            .contains("authentication is required");

                                    if (is401) {
                                        response.setStatus(401);
                                        response.getWriter().write(
                                                Ut.json.toString(
                                                        new RsData<>("401-1", "사용자 인증정보가 올바르지 않습니다.")
                                                )
                                        );
                                        return;
                                    }

                                    response.setStatus(403);
                                    response.getWriter().write(
                                            Ut.json.toString(
                                                    new RsData<>("403-1", request.getRequestURI() + ", " +
                                                            authException.getLocalizedMessage())
                                            )
                                    );
                                }
                        ))
        ;

        return http.build();
    }
}
