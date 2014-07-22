package de.unipotsdam.cs.groupplaner.domain.dates;

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

	@JsonIgnore
	public Integer getStartWeekday() {
		return (int) Math.floor(start / (24 * 60));
	}

	@JsonIgnore
	public Integer getStartHour() {
		return (int) Math.floor((start - (getStartWeekday() * 24 * 60)) / 60);
	}

	@JsonIgnore
	public Integer getStartMinute() {
		return start - (getStartWeekday() * 24 * 60) - getStartHour() * 60;
	}

	@JsonIgnore
	public Integer getEndWeekday() {
		return (int) Math.floor(end / (24 * 60));
	}

	@JsonIgnore
	public Integer getEndHour() {
		return (int) Math.floor((end - (getEndWeekday() * 24 * 60)) / 60);
	}

	@JsonIgnore
	public Integer getEndMinute() {
		return start - (getEndWeekday() * 24 * 60) - getEndHour() * 60;
	}
}
