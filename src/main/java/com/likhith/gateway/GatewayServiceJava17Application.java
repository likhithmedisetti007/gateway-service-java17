package com.likhith.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class GatewayServiceJava17Application {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceJava17Application.class, args);
	}

}