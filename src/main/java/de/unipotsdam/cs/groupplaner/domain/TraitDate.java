package de.unipotsdam.cs.groupplaner.domain;


import com.google.common.collect.ImmutableList;

import java.util.List;

public class TraitDate extends PeriodDate {

	public static final String TRAIT_ACCEPTED_DATE = "ACCEPTED_DATE";
	public static final String TRAIT_BLOCKED_DATE = "BLOCKED_DATE";

	/**
	 * A list of traits expressed as strings.
	 */
	private final ImmutableList<String> traits;

	public TraitDate(Integer start, Integer end, List<String> traits) {
		super(start, end);

		if (traits != null) {
			this.traits = ImmutableList.copyOf(traits);
		} else {
			this.traits = ImmutableList.of();
		}
	}

	public TraitDate(Integer start, Integer end) {
		this(start, end, null);
	}

	public TraitDate(PeriodDate periodDate, List<String> traits) {
		this(periodDate.getStart(), periodDate.getEnd(), traits);
	}

	public TraitDate(PeriodDate periodDate) {
		this(periodDate, null);
	}

	public ImmutableList<String> getTraits() {
		return traits;
	}

}
