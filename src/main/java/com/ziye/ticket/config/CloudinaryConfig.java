package com.ziye.ticket.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud_name:${CLOUDINARY_CLOUD_NAME:your_cloud_name}}")
    private String cloudName;

    @Value("${cloudinary.api_key:${CLOUDINARY_API_KEY:your_api_key}}")
    private String apiKey;

    @Value("${cloudinary.api_secret:${CLOUDINARY_API_SECRET:your_api_secret}}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = Map.of(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        );
        return new Cloudinary(config);
    }
}
