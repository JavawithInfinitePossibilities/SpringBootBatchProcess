/**
 * 
 */
package com.sid.spring.boot.batch.configuration;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.sid.spring.boot.batch.module.db.Customer;

/**
 * @author Lenovo
 *
 */
@Configuration
public class AsyncItemProcessorItemWriterApplicationJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;

	

	@Bean
	public JdbcPagingItemReader<Customer> pagingItemReader() {
		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
		reader.setDataSource(this.dataSource);
		reader.setFetchSize(10);
		reader.setRowMapper((ResultSet resultSet, int i) -> {
			return new Customer(resultSet.getLong("id"), resultSet.getString("firstName"), 
					resultSet.getString("lastName"), resultSet.getDate("birthdate"));
		});
		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("id, firstName, lastName, birthdate");
		queryProvider.setFromClause("from customer");
		Map<String, Order> sortKeys = new HashMap<>(1);
		sortKeys.put("id", Order.ASCENDING);
		queryProvider.setSortKeys(sortKeys);
		reader.setQueryProvider(queryProvider);
		return reader;
	}

	@Bean
	public ItemWriter<Customer> customItemWriter() {
		return (customers) -> {
			for (Customer currentItem : customers) {
				System.out.println("Current Item : " + currentItem);
			}
		};
	}

	@Bean
	public JdbcBatchItemWriter<Customer> customerJDBCItemWriter() {
		JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
		itemWriter.setDataSource(this.dataSource);
		itemWriter.setSql("INSERT INTO newcustomer VALUES (:id, :firstName, :lastName, :birthdate)");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		itemWriter.afterPropertiesSet();
		return itemWriter;
	}
	
	@Bean
	public AsyncItemWriter asyncItemWriter() throws Exception {
		AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();
		asyncItemWriter.setDelegate(customerJDBCItemWriter());
		asyncItemWriter.afterPropertiesSet();
		return asyncItemWriter;
	}
	
	@Bean
	public ItemProcessor<Customer, Customer> itemProcesser() {
		ItemProcessor<Customer, Customer> processor = (Customer item) -> {
			Thread.sleep(new Random().nextInt(10));
			return new Customer(item.getId(), item.getFirstName().toUpperCase(), 
					item.getLastName().toUpperCase(), item.getBirthdate());
		};
		return processor;
	}
	
	@Bean
	public AsyncItemProcessor asyncItemProcessor() throws Exception {
		AsyncItemProcessor<Customer, Customer> asyncItemProcessor = new AsyncItemProcessor();
		asyncItemProcessor.setDelegate(itemProcesser());
		asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
		asyncItemProcessor.afterPropertiesSet();
		return asyncItemProcessor;
	}
	
	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory
				.get("asyncItemProcessorItemWriterApplication1Start")
				.<Customer, Customer>chunk(10)
				.reader(pagingItemReader())
				/*.<Customer, Customer>processor(itemProcesser())*/
				.<Customer, Customer>processor(asyncItemProcessor())
				/*.writer(customerJDBCItemWriter())*/
				.writer(asyncItemWriter())
				.taskExecutor(new SimpleAsyncTaskExecutor())
				.build();
	}

	@Bean
	public Job listenerJob() throws Exception {
		return jobBuilderFactory.get("AsyncItemProcessorItemWriterApplicationJobConfiguration").start(step1()).build();
	}

}
