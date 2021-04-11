/**
 * 
 */
package com.sid.spring.boot.batch.module.db;

import java.util.Date;

/**
 * @author Lenovo
 *
 */
public class Customer {

	private final long id;
	private final String firstName;
	private final String lastName;
	private final Date birthdate;

	/**
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param birthdate
	 */
	public Customer(long id, String firstName, String lastName, Date birthdate) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthdate = birthdate;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @return the birthdate
	 */
	public Date getBirthdate() {
		return birthdate;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", birthdate=" + birthdate + "]";
	}

}
