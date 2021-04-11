package com.sid.spring.boot.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBootBatchProject11Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootBatchProject11Application.class, args);
	}

}
