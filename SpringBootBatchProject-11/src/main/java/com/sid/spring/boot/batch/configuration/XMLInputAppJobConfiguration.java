/**
 * 
 */
package com.sid.spring.boot.batch.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.sid.spring.boot.batch.module.db.Customer;

/**
 * @author Lenovo
 *
 */
@Configuration
public class XMLInputAppJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;
	
	@Bean
	public StaxEventItemReader<Customer> customerCustomItemReader(){
		XStreamMarshaller unmarshaller=new XStreamMarshaller();
		Map<String, Class> aliases = new HashMap<>();
		aliases.put("customer", Customer.class);
		unmarshaller.setAliases(aliases);
		StaxEventItemReader<Customer> reader = new StaxEventItemReader<>();
		reader.setResource(new ClassPathResource("/data/customers.xml"));
		reader.setFragmentRootElementName("customer");
		reader.setUnmarshaller(unmarshaller);
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
	public Step step1() {
		return stepBuilderFactory.get("stepReaderCustomerXMLFile1Start")
				.<Customer, Customer>chunk(10)
				.reader(customerCustomItemReader())
				.writer(customItemWriter())
				.build();
	}

	@Bean
	public Job listenerJob() {
		return jobBuilderFactory.get("CustomCustomerReadXMLFileJobConfiguration").start(step1()).build();
	}

}
