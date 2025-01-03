package com.example.maraon.configuration;

import com.example.maraon.entity.user.UserRoleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 스프링 시큐리티에서는 비밀번호를 암호화해서 저장해야 함. 이때 사용할 암호화 클래스
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // csrf 보안 해제 (개발 환경에서 설정시 복잡성)
        http
                .csrf(csrf -> csrf.disable());

        // 접근 경로별 인가 설정
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/user/update/**").hasRole("USER")
                        .requestMatchers("/**").permitAll());

        // 로그인 방식 설정 Form 로그인 방식
        http
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    // 시큐리티 role 계층화
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role(UserRoleType.ADMIN.toString()).implies((UserRoleType.USER.toString()))
                .build();
    }
}
