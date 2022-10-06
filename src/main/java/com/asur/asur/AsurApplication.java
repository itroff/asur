package com.asur.asur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AsurApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsurApplication.class, args);
	}

}
