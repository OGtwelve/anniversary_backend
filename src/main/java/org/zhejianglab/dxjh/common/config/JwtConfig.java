package org.zhejianglab.dxjh.common.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zhejianglab.dxjh.common.utils.JwtProps;
import org.zhejianglab.dxjh.common.utils.JwtUtil;

@Configuration
@EnableConfigurationProperties(JwtProps.class)
public class JwtConfig {

    @Bean
    public JwtUtil jwtUtil(JwtProps props) {
        return new JwtUtil(props.getSecret(), props.getExpireMinutes());
    }
}
