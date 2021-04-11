/**
 * 
 */
package com.sid.spring.boot.batch.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lenovo
 *
 */
@Configuration
@EnableBatchProcessing
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
			System.out.println("This is step 1:Starting");
			Thread.sleep(5000);
			System.out.println("This is step 1:Ending");
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
			System.out.println("This is step 2:Starting");
			Thread.sleep(5000);
			System.out.println("This is step 2:Ending");
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3").tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
			System.out.println("This is step 3:Starting");
			Thread.sleep(5000);
			System.out.println("This is step 3:Ending");
			return RepeatStatus.FINISHED;
		}).build();
	}

	/*
	@Bean
	public Job job() {
		return jobBuilderFactory.get("SecondSpringBatchProject")
				.start(step1())
				.next(step2())
				.next(step3())
				.build();
	}
	*/
	
	/*
	@Bean
	public Job job() {
		return jobBuilderFactory.get("ThirdSpringBatchProject")
				.start(step1()).on("COMPLETED").to(step2())
				.from(step2()).on("COMPLETED").to(step3())
				.from(step3()).end()
				.build();
	}
	*/
	
	/*
	@Bean
	public Job job() {
		return jobBuilderFactory.get("FourthSpringBatchProject")
				.start(step1()).on("COMPLETED").to(step2())
				.from(step2()).on("COMPLETED").fail()
				.from(step3()).end()
				.build();
	}
	*/
	
	@Bean
	public Job job() {
		return jobBuilderFactory.get("FifthSpringBatchProject")
				.start(step1()).on("COMPLETED").to(step2())
				.from(step2()).on("COMPLETED").stopAndRestart(step3())
				.from(step3()).end()
				.build();
	}
}
