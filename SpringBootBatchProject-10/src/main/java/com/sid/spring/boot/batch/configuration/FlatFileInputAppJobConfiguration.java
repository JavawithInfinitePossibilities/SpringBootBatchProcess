/**
 * 
 */
package com.sid.spring.boot.batch.configuration;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.sid.spring.boot.batch.module.db.Customer;

/**
 * @author Lenovo
 *
 */
@Configuration
public class FlatFileInputAppJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;
	
	@Bean
	public FlatFileItemReader<Customer> customerCustomItemReader(){
		FlatFileItemReader<Customer> flatFileItemReader=new FlatFileItemReader<Customer>();
		flatFileItemReader.setLinesToSkip(1);
		Resource resource = new ClassPathResource("/data/customer.csv");
		flatFileItemReader.setResource(resource);
		DefaultLineMapper<Customer> defaultLineMapper=new DefaultLineMapper<Customer>();
		
		DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();
		lineTokenizer.setNames("id","firstName","lastName","birthdate");
		
		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper((FieldSet fieldSet)->{
			return new Customer(fieldSet.readLong("id"),
					fieldSet.readString("firstName"),
					fieldSet.readString("lastName"),
					fieldSet.readDate("birthdate", "dd-MM-yyyy HH:mm"));
		});
		defaultLineMapper.afterPropertiesSet();
		flatFileItemReader.setLineMapper(defaultLineMapper);
		return flatFileItemReader;
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
	public Step step1() {
		return stepBuilderFactory.get("stepReaderCustomerFlatFile1Start")
				.<Customer, Customer>chunk(10)
				.reader(customerCustomItemReader())
				.writer(customItemWriter())
				.build();
	}

	@Bean
	public Job listenerJob() {
		return jobBuilderFactory.get("CustomCustomerReadFlatFileJobConfiguration").start(step1()).build();
	}

}
