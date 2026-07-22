package com.eventbook.EventHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
//@EnableCaching
public class EventHubApplication {

	public static void main(String[] args) {

		SpringApplication.run(EventHubApplication.class, args);
	}

}
