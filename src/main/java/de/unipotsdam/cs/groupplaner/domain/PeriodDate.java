package de.unipotsdam.cs.groupplaner.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PeriodDate {

	public static final int END_OF_WEEK = 72359;
	public static final int START_OF_WEEK = 10000;

	/**
	 * The starting point represented as 5 digit integer.
	 * 1. 		digit: 	weekday (1=monday , ... , 7=sunday)
	 * 2. + 3. 	digit: 	hour in 24h format
	 * 4. + 5. 	digit: 	minutes
	 */
	protected final Integer start;
	/**
	 * The ending point represented as 5 digit integer.
	 * 1. 		digit: 	weekday (1=monday , ... , 7=sunday)
	 * 2. + 3. 	digit: 	hour in 24h format
	 * 4. + 5. 	digit: 	minutes
	 */
	protected final Integer end;

	public PeriodDate(final Integer start, final Integer end) {
		this.start = start;
		this.end = end;
	}

	public Integer getStart() {
		return start;
	}

	public Integer getEnd() {
		return end;
	}

}
