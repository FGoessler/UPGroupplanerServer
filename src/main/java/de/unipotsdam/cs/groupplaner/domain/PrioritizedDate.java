package de.unipotsdam.cs.groupplaner.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class PrioritizedDate extends TraitDate {

	public static final int PRIORITY_OPTIMAL = 10;
	public static final int PRIORITY_NEUTRAL = 0;
	public static final int PRIORITY_BLOCKED = -10;

	/**
	 * A value between -10 and +10, were 0 is a neutral priority.
	 */
	private final Integer priority;

	public PrioritizedDate(Integer start, Integer end, Integer priority, List<String> traits) {
		super(start, end, traits);

		if (priority < PRIORITY_BLOCKED) {
			this.priority = PRIORITY_BLOCKED;
		} else if (priority > PRIORITY_OPTIMAL) {
			this.priority = PRIORITY_OPTIMAL;
		} else {
			this.priority = priority;
		}
	}

	public PrioritizedDate(Integer start, Integer end, Integer priority) {
		this(start, end, priority, null);
	}

	public PrioritizedDate(PeriodDate periodDate, Integer priority, List<String> traits) {
		this(periodDate.getStart(), periodDate.getEnd(), priority, traits);
	}

	public PrioritizedDate(PeriodDate periodDate, Integer priority) {
		this(periodDate, priority, null);
	}

	public Integer getPriority() {
		return priority;
	}
}
