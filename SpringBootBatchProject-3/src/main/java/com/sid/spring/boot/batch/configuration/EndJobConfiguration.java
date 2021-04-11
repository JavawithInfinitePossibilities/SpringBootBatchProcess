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
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lenovo
 *
 */
@Configuration
@EnableBatchProcessing
public class EndJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Step step5() {
		return stepBuilderFactory.get("step5").tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
			System.out.println("This is step 5:Starting");
			Thread.sleep(5000);
			System.out.println("This is step 5:Ending");
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Job lastFlowJob(@Qualifier("lastFlow") Flow flow) {
		return jobBuilderFactory.get("SixthLastSpringBatchProject")
				.start(step5()).on("COMPLETED").to(flow)
				.end()
				.build();
	}
}
