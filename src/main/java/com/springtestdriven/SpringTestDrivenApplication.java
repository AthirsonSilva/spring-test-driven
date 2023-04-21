package com.springtestdriven;

import com.github.javafaker.Faker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringTestDrivenApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringTestDrivenApplication.class, args);
	}

	@Bean
	public Faker faker() {
		return new Faker();
	}
}
