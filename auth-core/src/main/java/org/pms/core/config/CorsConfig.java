package org.pms.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 * 解决前端请求跨域问题
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2025/12/02
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 允许的源，默认为*，可通过配置文件修改
     */
    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    /**
     * 允许的请求方法
     */
    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    /**
     * 允许的请求头
     */
    @Value("${cors.allowed-headers:*}")
    private String allowedHeaders;

    /**
     * 是否允许携带凭证（cookies等）
     */
    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    /**
     * 预检请求缓存时间（秒）
     */
    @Value("${cors.max-age:3600}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOrigins.split(","))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders(allowedHeaders.split(","))
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }
}

