/**
 * 
 */
package com.sid.spring.boot.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * @author Lenovo
 *
 */
@Component
public class CustomJobListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("-------> Before Job execution <----------");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("-------> After Job execution <----------");
	}

}
