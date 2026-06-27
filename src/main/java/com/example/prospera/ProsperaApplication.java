package com.example.prospera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProsperaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProsperaApplication.class, args);
	}

}
