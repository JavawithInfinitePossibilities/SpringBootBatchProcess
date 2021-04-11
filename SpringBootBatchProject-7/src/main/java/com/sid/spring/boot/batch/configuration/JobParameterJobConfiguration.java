/**
 * 
 */
package com.sid.spring.boot.batch.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	@StepScope
	public Tasklet helloWorldTasklet(@Value("#{jobParameters['message']}") String message) {
		return (StepContribution contribution, ChunkContext chunkContext) -> {
			System.out.println("This is step in Start flow step1Start Job parameter : Starting --->"
					+ String.format("%s has been executed on thread %s ", chunkContext.getStepContext().getStepName(), Thread.currentThread().getName()));
			System.out.println(message);
			Thread.sleep(5000);
			System.out.println("This is step in Ending flow step1Start Job parameter : Ending --->"
					+ String.format("%s has been executed on thread %s ", chunkContext.getStepContext().getStepName(), Thread.currentThread().getName()));
			return RepeatStatus.FINISHED;
		};
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1ListenerStart").tasklet(helloWorldTasklet(null)).build();
	}

	@Bean
	public Job listenerJob() {
		return jobBuilderFactory.get("JobParameterJobConfiguration").start(step1()).build();
	}

}
