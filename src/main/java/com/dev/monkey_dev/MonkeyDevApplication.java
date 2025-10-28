package com.dev.monkey_dev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ConfigurationPropertiesScan("com.dev.monkey_dev.properties")
@EnableScheduling
@EnableRetry
@EnableJpaAuditing
public class MonkeyDevApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonkeyDevApplication.class, args);
	}

}
