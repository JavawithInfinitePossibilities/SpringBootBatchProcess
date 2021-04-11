/**
 * 
 */
package com.sid.spring.boot.batch.configuration;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
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
public class FirstFlowConfiguration {
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
			System.out.println("This is step in Start flow 1:Starting");
			Thread.sleep(5000);
			System.out.println("This is step in Start flow 1:Ending");
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
			System.out.println("This is step in Start flow 2:Starting");
			Thread.sleep(5000);
			System.out.println("This is step in Start flow 2:Ending");
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3").tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
			System.out.println("This is step in Start flow 3:Starting");
			Thread.sleep(5000);
			System.out.println("This is step in Start flow 3:Ending");
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Flow firstFlow() {
		FlowBuilder<Flow> flowBuilder = new FlowBuilder<Flow>("firstFlow");
		flowBuilder.start(step1()).next(step2()).next(step3()).end();
		return flowBuilder.build();
	}
}
