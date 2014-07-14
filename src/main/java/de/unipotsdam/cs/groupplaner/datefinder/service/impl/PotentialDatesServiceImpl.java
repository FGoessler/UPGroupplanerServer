package de.unipotsdam.cs.groupplaner.datefinder.service.impl;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.datefinder.list.LinearDateList;
import de.unipotsdam.cs.groupplaner.datefinder.service.DatesAggregationService;
import de.unipotsdam.cs.groupplaner.datefinder.service.PotentialDatesService;
import de.unipotsdam.cs.groupplaner.datefinder.service.PrioritizeDatesService;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
		final LinearDateList dates = datesAggregationService.loadDates(groupId);

		final List<PrioritizedDate> prioritizedDates = prioritizeDatesService.prioritizeDates(dates);

		return ImmutableList.copyOf(prioritizedDates);
	}

}
