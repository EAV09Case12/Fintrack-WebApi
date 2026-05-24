package com.example.fintrack_webapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class FintrackWebapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FintrackWebapiApplication.class, args);
	}

}
