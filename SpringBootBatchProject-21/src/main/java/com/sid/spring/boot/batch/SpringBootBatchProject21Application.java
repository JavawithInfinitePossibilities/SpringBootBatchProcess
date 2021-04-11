package com.sid.spring.boot.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableBatchProcessing()
public class SpringBootBatchProject21Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootBatchProject21Application.class, args);
	}

}
