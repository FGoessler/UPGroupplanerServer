package de.unipotsdam.cs.groupplaner.datefinder.service.impl;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.datefinder.service.DatesAggregationService;
import de.unipotsdam.cs.groupplaner.datefinder.service.PotentialDatesService;
import de.unipotsdam.cs.groupplaner.datefinder.service.PrioritizeDatesService;
import de.unipotsdam.cs.groupplaner.domain.PeriodDate;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PotentialDatesServiceImpl implements PotentialDatesService {

	@Autowired
	private DatesAggregationService datesAggregationService;
	@Autowired
	private PrioritizeDatesService prioritizeDatesService;

	@Override
	@PreAuthorize("@groupPermissionService.hasReadPermission(authentication, #groupId)")
	public ImmutableList<PrioritizedDate> calculatePotentialDates(final Integer groupId) {
		final List<TraitDate> allBlockedDates = datesAggregationService.getSortedBlockedDatesOfAllMembers(groupId);

		// DO we really need this? Couldn't we directly resolve the free dates by iterating over all blocked dates and consider overlapping in the same step?
		// This might make the QoS analysis easier?!
		final List<TraitDate> combinedBlockedDates = datesAggregationService.combineOverlappingDates(allBlockedDates);

		final List<TraitDate> availableDates = calculateAvailableDates(combinedBlockedDates);

		final List<PrioritizedDate> prioritizedDates = prioritizeDatesService.prioritizeDates(combinedBlockedDates, availableDates);

		return ImmutableList.copyOf(prioritizedDates);
	}

	private List<TraitDate> calculateAvailableDates(final List<TraitDate> allBlockedDates) {
		final List<TraitDate> availableDates = new ArrayList<TraitDate>();
		int curPeriodStart = PeriodDate.START_OF_WEEK;
		for (PeriodDate periodDate : allBlockedDates) {
			if (periodDate.getStart() == curPeriodStart) {
				curPeriodStart = periodDate.getEnd();
			} else {
				availableDates.add(new TraitDate(curPeriodStart, periodDate.getStart()));
				curPeriodStart = periodDate.getEnd();
			}
		}
		if (curPeriodStart != PeriodDate.END_OF_WEEK) {
			availableDates.add(new TraitDate(curPeriodStart, PeriodDate.END_OF_WEEK));
		}
		return availableDates;
	}

}
