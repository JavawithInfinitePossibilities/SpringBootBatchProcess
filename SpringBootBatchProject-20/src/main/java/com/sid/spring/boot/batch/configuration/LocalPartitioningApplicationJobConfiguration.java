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
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.sid.spring.boot.batch.module.db.Customer;
import com.sid.spring.boot.batch.module.dto.ColumnRangePartitioner;

/**
 * @author Lenovo
 *
 */
@Configuration
public class LocalPartitioningApplicationJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;

	@Bean
	@StepScope
	public JdbcPagingItemReader<Customer> pagingItemReader(@Value("#{stepExecutionContext['minValue']}")Long minValue,
			@Value("#{stepExecutionContext['maxValue']}")Long maxValue) {
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
	@StepScope
	public ItemWriter<Customer> customItemWriter() {
		return (customers) -> {
			for (Customer currentItem : customers) {
				System.out.println("Current Item : " + currentItem);
			}
		};
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<Customer> customerJDBCItemWriter() {
		JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
		itemWriter.setDataSource(this.dataSource);
		itemWriter.setSql("INSERT INTO newcustomer VALUES (:id, :firstName, :lastName, :birthdate)");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		itemWriter.afterPropertiesSet();
		return itemWriter;
	}
	
	@Bean
	@StepScope
	public ItemProcessor<Customer, Customer> itemProcesser() {
		ItemProcessor<Customer, Customer> processor = (Customer item) -> {
			Thread.sleep(new Random().nextInt(10));
			return new Customer(item.getId(), item.getFirstName().toUpperCase(), 
					item.getLastName().toUpperCase(), item.getBirthdate());
		};
		return processor;
	}
	
	@Bean
	public ColumnRangePartitioner partitioner() {
		ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();
		columnRangePartitioner.setColumn("id");
		columnRangePartitioner.setDataSource(this.dataSource);
		columnRangePartitioner.setTable("customer");
		return columnRangePartitioner;
	}
	
	@Bean
	public Step slaveStep() {
		return stepBuilderFactory.get("slaveStep")
				.<Customer, Customer>chunk(1000)
				.reader(pagingItemReader(null, null))
				.processor(itemProcesser())
				.writer(customerJDBCItemWriter())
				.build();
	}
	
	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory
				.get("LocalPartitioningApplication1Start")
				.partitioner(slaveStep().getName(), partitioner())
				.step(slaveStep())
				.gridSize(4)
				.taskExecutor(new SimpleAsyncTaskExecutor())
				.build();
	}

	@Bean
	public Job listenerJob() throws Exception {
		return jobBuilderFactory.get("LocalPartitioningApplicationJobConfiguration").start(step1()).build();
	}

}
