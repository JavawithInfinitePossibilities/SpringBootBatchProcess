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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sid.spring.boot.batch.ListItemReader;
import com.sid.spring.boot.batch.listener.ChunkListener;
import com.sid.spring.boot.batch.listener.CustomJobListener;

/**
 * @author Lenovo
 *
 */
@Configuration
public class ListenerJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	
	
	@Bean
	public ItemReader<String> reader() {
		return new ListItemReader<String>(Arrays.asList("One", "Two", "Three", "Four"));
	}

	@Bean
	public ItemWriter<String> writer() {
		return ((List<? extends String> items) -> {
			for (String item : items) {
				System.out.println("Writer is writting the items : " + item);
			}
		});
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1ListenerStart").<String, String>chunk(2).faultTolerant().listener(
				new ChunkListener()).reader(reader()).writer(writer()).build();
	}

	@Bean
	public Job listenerJob() {
		return jobBuilderFactory.get("ListenerJobConfiguration").start(step1()).listener(new CustomJobListener())
				.build();
	}

}
