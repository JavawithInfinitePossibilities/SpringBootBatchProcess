/**
 * 
 */
package com.sid.spring.boot.batch.configuration;

import java.io.File;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sid.spring.boot.batch.module.db.Customer;
import com.sid.spring.boot.batch.module.dto.CustomerRowMapper;

/**
 * @author Lenovo
 *
 */
@Configuration
public class DBInputAppJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;

	@Bean
	public JdbcCursorItemReader<Customer> customItemReader() {
		JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<>();
		reader.setSql("select id, firstName, lastName, birthdate from customer order by lastName, firstName");
		reader.setDataSource(this.dataSource);
		reader.setRowMapper(new CustomerRowMapper());
		reader.setRowMapper((ResultSet resultSet, int i) -> {
			return new Customer(resultSet.getLong("id"), resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getDate("birthdate"));
		});
		return reader;
	}

	@Bean
	public JdbcPagingItemReader<Customer> pagingItemReader() {
		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
		reader.setDataSource(this.dataSource);
		reader.setFetchSize(10);
		reader.setRowMapper((ResultSet resultSet, int i) -> {
			return new Customer(resultSet.getLong("id"), resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getDate("birthdate"));
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

	//@Bean
	public StaxEventItemWriter<Customer> customerXMLItemWriter() throws Exception {
		XStreamMarshaller marshaller = new XStreamMarshaller();
		Map<String, Class> aliases = new HashMap<>();
		aliases.put("customer", Customer.class);
		marshaller.setAliases(aliases);
		StaxEventItemWriter<Customer> itemWriter = new StaxEventItemWriter<>();
		itemWriter.setRootTagName("customers");
		itemWriter.setMarshaller(marshaller);
		String customerOutputPath = File.createTempFile("customerOutput", ".xml").getAbsolutePath();
		System.out.println(">> Output Path: " + customerOutputPath);
		itemWriter.setResource(new FileSystemResource(customerOutputPath));
		itemWriter.afterPropertiesSet();
		return itemWriter;
	}

	
	//@Bean
	public FlatFileItemWriter<Customer> customerJSONItemWriter() throws Exception {
		FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();
		ObjectMapper objectMapper = new ObjectMapper();
		itemWriter.setLineAggregator((Customer item) -> {
			try {
				return objectMapper.writeValueAsString(item);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Unable to serialize Customer", e);
			}
		});
		String customerOutputPath = File.createTempFile("customerOutput", ".out").getAbsolutePath();
		System.out.println(">> Output Path: " + customerOutputPath);
		itemWriter.setResource(new FileSystemResource(customerOutputPath));
		itemWriter.afterPropertiesSet();
		return itemWriter;
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
	public ItemProcessor<Customer, Customer> itemProcesser() {
		ItemProcessor<Customer, Customer> processor = (Customer item) -> {
			return new Customer(item.getId(), item.getFirstName().toUpperCase(), 
					item.getLastName().toUpperCase(), item.getBirthdate());
		};
		return processor;
	}
	
	@Bean
	public ItemProcessor<Customer, Customer> filterItemProcesser() {
		ItemProcessor<Customer, Customer> processor = (Customer item) -> {
			if (item.getId() % 2 == 0) {
				return null;
			} else {
				return item;
			}
		};
		return processor;
	}
	
	@Bean
	public ValidatingItemProcessor<Customer> validationItemProcessor() {
		ValidatingItemProcessor<Customer> customerValidatingItemProcessor = new ValidatingItemProcessor<>((Customer value) -> {
			if (value.getFirstName().startsWith("A")) {
				throw new ValidationException("First names that begin with A are invalid: " + value);
			}
		});
		customerValidatingItemProcessor.setFilter(true);
		return customerValidatingItemProcessor;
	}
	
	@Bean
	public CompositeItemProcessor<Customer, Customer> compositeItemProcesser() throws Exception {
		List<ItemProcessor<Customer, Customer>> delegates = Arrays.asList(filterItemProcesser(), itemProcesser());
		CompositeItemProcessor<Customer, Customer> compositeItemProcessor = new CompositeItemProcessor<Customer, Customer>();
		compositeItemProcessor.setDelegates(delegates);
		compositeItemProcessor.afterPropertiesSet();
		return compositeItemProcessor;
	}
	
	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory
				.get("multithreadedStepApplication1Start")
				.<Customer, Customer>chunk(10)
				/*.reader(customItemReader())*/
				.reader(pagingItemReader())
				.<Customer, Customer>processor(itemProcesser())
				/*.<Customer, Customer>processor(filterItemProcesser())*/
				/*.<Customer, Customer>processor(validationItemProcessor())*/
				/*.<Customer, Customer>processor(compositeItemProcesser())*/
				/*.writer(customItemWriter())*/
				/*.writer(customerXMLItemWriter())*/
				/*.writer(customerJSONItemWriter())*/
				.writer(customerJDBCItemWriter())
				.taskExecutor(new SimpleAsyncTaskExecutor())
				.build();
	}

	@Bean
	public Job listenerJob() throws Exception {
		return jobBuilderFactory.get("MultithreadedStepApplicationJobConfiguration").start(step1()).build();
	}

}
