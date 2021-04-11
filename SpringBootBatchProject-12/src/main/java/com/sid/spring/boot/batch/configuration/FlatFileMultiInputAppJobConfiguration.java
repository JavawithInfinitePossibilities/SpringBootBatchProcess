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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class FlatFileMultiInputAppJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;

	@Value("classpath*:/data/customer*.csv")
	private Resource[] inputFiles;

	@Bean
	public MultiResourceItemReader<Customer> multiResourceItemReader() {
		MultiResourceItemReader<Customer> itemReader = new MultiResourceItemReader<Customer>();
		itemReader.setDelegate(customerCustomItemReader());
		itemReader.setResources(inputFiles);
		return itemReader;
	}
	
	@Bean
	public FlatFileItemReader<Customer> customerCustomItemReader(){
		FlatFileItemReader<Customer> flatFileItemReader=new FlatFileItemReader<Customer>();
		flatFileItemReader.setLinesToSkip(1);
		DefaultLineMapper<Customer> defaultLineMapper=new DefaultLineMapper<Customer>();		
		DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();
		lineTokenizer.setNames("id","firstName","lastName","birthdate");
		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper((FieldSet fieldSet)->{
			return new Customer(fieldSet.readLong("id"),
					fieldSet.readString("firstName"),
					fieldSet.readString("lastName"),
					fieldSet.readDate("birthdate", "yyyy-MM-dd HH:mm"));
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
		return stepBuilderFactory.get("stepReaderCustomerFlatFileMulti1Start")
				.<Customer, Customer>chunk(10)
				.reader(multiResourceItemReader())
				.writer(customItemWriter())
				.build();
	}

	@Bean
	public Job listenerJob() {
		return jobBuilderFactory.get("CustomCustomerReadFlatFileMultiJobConfiguration").start(step1()).build();
	}

}
