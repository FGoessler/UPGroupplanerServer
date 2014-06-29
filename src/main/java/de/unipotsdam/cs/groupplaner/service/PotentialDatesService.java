package de.unipotsdam.cs.groupplaner.service;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;

public interface PotentialDatesService {
	public ImmutableList<PrioritizedDate> calculatePotentialDates(final Integer groupId);
}
