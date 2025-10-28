package com.dev.monkey_dev.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "file")
public class FileInfoConfig {

    private String tempPath;

    private String serverPath;

    private String clientPath;

    private String baseUrl;

}
