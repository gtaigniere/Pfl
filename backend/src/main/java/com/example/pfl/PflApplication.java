package com.example.pfl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PflApplication {

	public static void main(String[] args) {
		SpringApplication.run(PflApplication.class, args);
	}

}
