package de.unipotsdam.cs.groupplaner.domain;


public class PrioritizedDate extends PeriodDate {
	/**
	 * A value between -10 and +10, were 0 is a neutral priority.
	 */
	private final Integer priority;

	public PrioritizedDate(Integer start, Integer end, Integer priority) {
		super(start, end);
		this.priority = priority;
	}

	public PrioritizedDate(PeriodDate periodDate, Integer priority) {
		super(periodDate.getStart(), periodDate.getEnd());
		this.priority = priority;
	}

	public Integer getPriority() {
		return priority;
	}
}
