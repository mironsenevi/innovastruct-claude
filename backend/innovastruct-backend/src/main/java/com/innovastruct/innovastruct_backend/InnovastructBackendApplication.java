package com.innovastruct.innovastruct_backend;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class InnovastructBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(InnovastructBackendApplication.class, args);
	}
}
