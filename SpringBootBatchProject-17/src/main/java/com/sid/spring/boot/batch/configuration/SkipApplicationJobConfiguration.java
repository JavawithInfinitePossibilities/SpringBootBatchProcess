/**
 * 
 */
package com.sid.spring.boot.batch.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.sid.spring.boot.batch.component.CustomException;
import com.sid.spring.boot.batch.component.CustomSkipListener;
import com.sid.spring.boot.batch.component.SkipItemProcessor;
import com.sid.spring.boot.batch.component.SkipItemWriter;

/**
 * @author Lenovo
 *
 */
@Configuration
public class SkipApplicationJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	@StepScope
	public ListItemReader<String> reader() {
		List<String> items = new ArrayList<>();
		for(int i = 0; i < 100; i++) {
			items.add(String.valueOf(i));
		}
		return new ListItemReader<>(items);
	}

	@Bean
	@StepScope
	public SkipItemProcessor processor() {
		SkipItemProcessor processor = new SkipItemProcessor();
		return processor;
	}

	@Bean
	@StepScope
	public SkipItemWriter writer() {
		SkipItemWriter writer = new SkipItemWriter();
		return writer;
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory
				.get("skipRetryListenersApplicationStep1")
				.<String,String> chunk(10)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.faultTolerant()
				.skip(CustomException.class)
				.skipLimit(15)
				.listener(new CustomSkipListener())
				.build();
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("FirstSkipRetryListenersApplicationSpringBatchProject").start(step1()).build();
	}
}
