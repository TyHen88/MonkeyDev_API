package com.dev.monkey_dev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = { OAuth2ClientAutoConfiguration.class })
@ConfigurationPropertiesScan("com.dev.monkey_dev.properties")
@EnableScheduling
@EnableRetry

public class MonkeyDevApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonkeyDevApplication.class, args);
	}

}
