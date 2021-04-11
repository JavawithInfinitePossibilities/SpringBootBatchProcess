/**
 * 
 */
package com.sid.spring.boot.batch.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sid.spring.boot.batch.processer.reader.StatelessItemReader;

/**
 * @author Lenovo
 *
 */
@Configuration
public class JobParameterJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public StatelessItemReader statelessItemReader() {
		List<String> stringsList = Arrays.asList("One", "Two", "Three", "Four");
		return new StatelessItemReader(stringsList);

	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("stepReader1ListenerStart")
				.<String, String>chunk(2)
				.reader(statelessItemReader())
				.writer((list) -> {
					for (String currentItem : list) {
						System.out.println("Current Item : "+currentItem);
					}
				})
				.build();
	}

	@Bean
	public Job listenerJob() {
		return jobBuilderFactory.get("ReadJobConfiguration").start(step1()).build();
	}

}
