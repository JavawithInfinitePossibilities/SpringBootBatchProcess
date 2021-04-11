/**
 * 
 */
package com.sid.spring.boot.batch.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lenovo
 *
 */
@Configuration
public class StatefulInputAppJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;

	@Bean
	@StepScope
	public StatefulItemReader itemReader() {
		List<String> items = new ArrayList<>(100);
		for(int i = 1; i <= 100; i++) {
			items.add(String.valueOf(i));
		}
		return new StatefulItemReader(items);
	}
	
	@Bean
	public ItemWriter itemWriter() {
		return new ItemWriter<String>() {
			@Override
			public void write(List<? extends String> items) throws Exception {
				System.out.println("The size of this chunk was :" + items.size());
				for (String item : items) {
					System.out.println(">> " + item);
				}
			}
		};
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("stepReaderStateFul1Start")
				.<String, String>chunk(10)
				.reader(itemReader())
				.writer(itemWriter())
				.stream(itemReader())
				.build();
	}

	@Bean
	public Job listenerJob() {
		return jobBuilderFactory.get("CustomReadStateFulJobConfiguration").start(step1()).build();
	}

}
