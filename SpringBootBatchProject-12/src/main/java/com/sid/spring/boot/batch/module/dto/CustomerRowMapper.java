package com.sid.spring.boot.batch.module.dto;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.sid.spring.boot.batch.module.db.Customer;

/**
 * @author Lenovo
 */
public class CustomerRowMapper implements FieldSetMapper<Customer> {

	@Override
	public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
		return new Customer(fieldSet.readLong("id"),
				fieldSet.readString("firstName"),
				fieldSet.readString("lastName"),
				fieldSet.readDate("birthdate", "yyyy-MM-dd HH:mm"));
	}
	
}
