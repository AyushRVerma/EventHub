package com.eventbook.EventHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EntityScan(basePackages = "com.eventbook.EventHub.domain.entity")
@EnableJpaRepositories(basePackages = "com.eventbook.EventHub.repositories")
public class EventHubApplication {

	public static void main(String[] args) {

		SpringApplication.run(EventHubApplication.class, args);
	}

}
