package de.unipotsdam.cs.groupplaner.datefinder.service;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.dates.PrioritizedDate;

public interface PotentialDatesService {
	public ImmutableList<PrioritizedDate> calculatePotentialDates(final Integer groupId);
}
