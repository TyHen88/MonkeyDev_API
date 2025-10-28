package com.dev.monkey_dev.config;

import com.dev.monkey_dev.properties.FileInfoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class to map image resource paths to physical server locations,
 * ensuring images are accessible via HTTP endpoints.
 */
@Configuration
public class FileConfiguration implements WebMvcConfigurer {

    private final FileInfoConfig fileInfoConfig;

    public FileConfiguration(FileInfoConfig fileInfoConfig) {
        this.fileInfoConfig = fileInfoConfig;
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        String imagePathPattern = "/api/wb/v1/image/**";
        String location = fileInfoConfig.getServerPath();
        if (location != null && !location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler(imagePathPattern)
                .addResourceLocations("file:" + location);
    }
}
