package com.dev.monkey_dev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MonkeyDevApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonkeyDevApplication.class, args);
	}

}
