package com.myportfolio.portfolio.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Webhook endpoint: no origin restriction — Monime's servers call this directly,
        // and the real protection here is HMAC signature verification, not CORS.
        CorsConfiguration webhookConfig = new CorsConfiguration();
        webhookConfig.setAllowedOriginPatterns(List.of("*"));
        webhookConfig.setAllowedMethods(List.of("POST", "OPTIONS"));
        webhookConfig.setAllowedHeaders(List.of("*"));
        source.registerCorsConfiguration("/api/support/webhook", webhookConfig);
        source.registerCorsConfiguration("/api/support/success", webhookConfig);

        // Everything else: restricted to your actual frontend domains, as before.
        CorsConfiguration generalConfig = new CorsConfiguration();
        generalConfig.setAllowedOriginPatterns(List.of("https://*.vercel.app", "http://localhost:*"));
        generalConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        generalConfig.setAllowedHeaders(List.of("*"));
        generalConfig.setAllowCredentials(false);
        source.registerCorsConfiguration("/api/**", generalConfig);

        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>(new CorsFilter(source));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}