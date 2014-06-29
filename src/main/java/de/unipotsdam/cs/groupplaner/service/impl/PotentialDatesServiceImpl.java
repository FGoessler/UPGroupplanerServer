package de.unipotsdam.cs.groupplaner.service.impl;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.PeriodDate;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;
import de.unipotsdam.cs.groupplaner.service.PotentialDatesService;
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
		final List<PeriodDate> allBlockedDates = datesAggregationService.getSortedBlockedDatesOfAllMembers(groupId);

		// cancel right here if we do not have any blocked dates
		if (allBlockedDates.size() == 0) {
			return ImmutableList.of(new PrioritizedDate(PeriodDate.START_OF_WEEK, PeriodDate.END_OF_WEEK, PrioritizedDate.PRIORITY_OPTIMAL));
		}

		// DO we really need this? Couldn't we directly resolve the free dates by iterating over all blocked dates and consider overlapping in the same step?
		// This might make the QoS analysis easier?!
		final List<PeriodDate> combinedBlockedDates = datesAggregationService.combineOverlappingDates(allBlockedDates);

		final List<PeriodDate> availableDates = calculateAvailableDates(combinedBlockedDates);

		final List<PrioritizedDate> prioritizedDates = prioritizeDatesService.prioritizeDates(combinedBlockedDates, availableDates);

		return ImmutableList.copyOf(prioritizedDates);
	}

	private List<PeriodDate> calculateAvailableDates(final List<PeriodDate> allBlockedDates) {
		final List<PeriodDate> availableDates = new ArrayList<PeriodDate>();
		int curPeriodStart = PeriodDate.START_OF_WEEK;
		for (PeriodDate periodDate : allBlockedDates) {
			if (periodDate.getStart() == curPeriodStart) {
				curPeriodStart = periodDate.getEnd();
			} else {
				availableDates.add(new PeriodDate(curPeriodStart, periodDate.getStart()));
				curPeriodStart = periodDate.getEnd();
			}
		}
		if (curPeriodStart != PeriodDate.END_OF_WEEK) {
			availableDates.add(new PeriodDate(curPeriodStart, PeriodDate.END_OF_WEEK));
		}
		return availableDates;
	}

}
