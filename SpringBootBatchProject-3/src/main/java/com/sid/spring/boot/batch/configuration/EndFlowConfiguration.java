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
public class EndFlowConfiguration {
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Step step6() {
		return stepBuilderFactory.get("step6").tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
			System.out.println("This is step in End flow 6:Starting");
			Thread.sleep(5000);
			System.out.println("This is step in End flow 6:Ending");
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Step step7() {
		return stepBuilderFactory.get("step7").tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
			System.out.println("This is step in End flow 7:Starting");
			Thread.sleep(5000);
			System.out.println("This is step in End flow 7:Ending");
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Step step8() {
		return stepBuilderFactory.get("step8").tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
			System.out.println("This is step in End flow 8:Starting");
			Thread.sleep(5000);
			System.out.println("This is step in End flow 8:Ending");
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Flow lastFlow() {
		FlowBuilder<Flow> flowBuilder = new FlowBuilder<Flow>("endFlow");
		flowBuilder.start(step6()).next(step7()).next(step8()).end();
		return flowBuilder.build();
	}
}
