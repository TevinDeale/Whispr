package com.tevind.whispr.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "jwt")
@Configuration
@Data
public class JwtConfig {
    private String secret;
    private long expiration;
}
