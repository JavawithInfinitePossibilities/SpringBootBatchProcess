/**
 * 
 */
package com.sid.spring.boot.batch.processer.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * @author Lenovo
 *
 */
public class StatelessItemReader implements ItemReader<String> {

	private final Iterator<String> itemReaderData;

	/**
	 * @param itemReaderData
	 */
	public StatelessItemReader(List<String> itemReaderData) {
		this.itemReaderData = itemReaderData.iterator();
	}

	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (this.itemReaderData.hasNext()) {
			return this.itemReaderData.next();
		} else {
			return null;
		}
	}

}
