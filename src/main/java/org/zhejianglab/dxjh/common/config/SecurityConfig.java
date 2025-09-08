package org.zhejianglab.dxjh.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zhejianglab.dxjh.common.filter.JwtAuthFilter;
import org.zhejianglab.dxjh.common.utils.JwtUtil;
import org.zhejianglab.dxjh.common.utils.SecurityPaths;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil; // 只注入 JwtUtil（或其他“不会反向依赖本配置”的 Bean）

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .cors().and()
                .authorizeRequests()
                .antMatchers(SecurityPaths.PUBLIC).permitAll()
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated();

        // 注册你的 JWT 过滤器
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS（按需修改允许的域名）
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        // 精确列出允许的来源（使用 allowCredentials(true) 时不能用 "*"）
        c.setAllowedOrigins(Arrays.asList(
                "http://192.168.3.38:3000",
                "https://dxjh.zhejianglab.org",
                "http://192.168.1.4:3000",
                "http://192.168.0.103:3000",
                "http://192.168.1.16:3000"
        ));
        c.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
        // 前端会带的头，包含 JWT/JSON 常用
        c.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","Accept","Origin","X-Requested-With"));
        // 如果需要带 cookie / Authorization
        c.setAllowCredentials(true);
        // 如果有下载文件，常见需要暴露的头
        c.setExposedHeaders(Collections.singletonList("Content-Disposition"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 你的后端接口前缀，如果都在 /api 下，也可以写 "/api/**"
        source.registerCorsConfiguration("/**", c);
        return source;
    }

    // 把 JwtAuthFilter 声明成 Bean（也可以在 JwtAuthFilter 上加 @Component）
    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtUtil jwtUtil) {
        return new JwtAuthFilter(jwtUtil);
    }
}
