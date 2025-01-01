package com.example.maraon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MaraonApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaraonApplication.class, args);
	}

}
