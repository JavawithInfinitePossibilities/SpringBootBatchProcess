/**
 * 
 */
package com.sid.spring.boot.batch.module.db;

import java.util.Date;

import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;

/**
 * @author Lenovo
 *
 */
public class Customer implements ResourceAware {

	private final long id;
	private final String firstName;
	private final String lastName;
	private final Date birthDate;

	private Resource resource;

	/**
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param birthDate
	 */
	public Customer(long id, String firstName, String lastName, Date birthDate) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
	}

	@Override
	public String toString() {
		return "Customer{" +
				"id=" + id +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", birthdate=" + birthDate +
				", from " + resource.getDescription() +
				'}';
	}

	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
