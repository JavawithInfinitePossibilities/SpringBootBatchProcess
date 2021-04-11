/**
 * 
 */
package com.sid.spring.boot.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.aop.support.AopUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * @author Lenovo
 *
 */
public class ListItemReader<T> implements ItemReader<T> {

	private List<T> list;

	/**
	 * 
	 */
	public ListItemReader() {
		super();
	}

	/**
	 * @param list
	 */
	public ListItemReader(List<T> list) {
		if (AopUtils.isAopProxy(list)) {
			this.list = list;
		} else {
			this.list = new ArrayList<T>(list);
		}
	}

	@Override
	public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (!list.isEmpty()) {
			System.out.println("-------> Reading the object <-----------");
			return list.remove(0);
		}
		return null;
	}

}
