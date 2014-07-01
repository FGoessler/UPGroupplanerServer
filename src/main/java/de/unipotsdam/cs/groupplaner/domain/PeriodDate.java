package de.unipotsdam.cs.groupplaner.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PeriodDate {

	public static final int END_OF_WEEK = 10080;    // 7 * 24 * 60
	public static final int START_OF_WEEK = 0;

	/**
	 * The starting point represented as minutes from beginning of the week (0:00 monday).
	 */
	protected final Integer start;
	/**
	 * The ending point represented as minutes from beginning of the week (0:00 monday).
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

	@JsonIgnore
	public Integer getDuration() {
		Integer duration = end - start;
		if (duration < 0) duration += END_OF_WEEK;
		return duration;
	}
}
