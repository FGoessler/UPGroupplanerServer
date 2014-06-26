package de.unipotsdam.cs.groupplaner.service;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.PeriodDate;

public interface PotentialDatesService {
	public ImmutableList<PeriodDate> calculatePotentialDates(final Integer groupId);
}
