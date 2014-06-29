package de.unipotsdam.cs.groupplaner.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PrioritizedDate extends PeriodDate {

	public static final int PRIORITY_OPTIMAL = 10;
	public static final int PRIORITY_NEUTRAL = 0;
	public static final int PRIORITY_BLOCKED = -10;

	/**
	 * A value between -10 and +10, were 0 is a neutral priority.
	 */
	private final Integer priority;

	public PrioritizedDate(Integer start, Integer end, Integer priority) {
		super(start, end);
		this.priority = priority;
	}

	public PrioritizedDate(PeriodDate periodDate, Integer priority) {
		this(periodDate.getStart(), periodDate.getEnd(), priority);
	}

	public Integer getPriority() {
		return priority;
	}
}
