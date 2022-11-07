package com.asur.asur;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AsurApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(AsurApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(AsurApplication.class, args);
	}

	@Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
        return Executors.newScheduledThreadPool(5);
    }


    @Bean
    ApplicationRunner applicationRunner(Environment environment) {
        return args -> {
            LOGGER.info("message from application.properties " + environment.getProperty("MQTT_ADDRESS"));
        };
    }

}
