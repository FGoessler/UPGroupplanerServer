package de.unipotsdam.cs.groupplaner.domain;


import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class TraitDate extends PeriodDate {

	public static final String TRAIT_ACCEPTED_DATE = "ACCEPTED_DATE";
	public static final String TRAIT_BLOCKED_DATE = "BLOCKED_DATE";

	/**
	 * A map of traits with strings as the key and a variable value. If you don't want to store a value just set the
	 * value to true.
	 */
	private final ImmutableMap<String, Object> traits;

	public TraitDate(Integer start, Integer end, Map<String, Object> traits) {
		super(start, end);

		if (traits != null) {
			this.traits = ImmutableMap.copyOf(traits);
		} else {
			this.traits = ImmutableMap.of();
		}
	}

	public TraitDate(PeriodDate periodDate, Map<String, Object> traits) {
		this(periodDate.getStart(), periodDate.getEnd(), traits);
	}

	public ImmutableMap<String, Object> getTraits() {
		return traits;
	}

	public Boolean hasTrait(final String trait) {
		return traits.get(trait) != null;
	}

	public Object getTrait(final String trait) {
		return traits.get(trait);
	}

}
