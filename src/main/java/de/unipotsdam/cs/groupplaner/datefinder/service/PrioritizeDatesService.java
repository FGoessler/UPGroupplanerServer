package de.unipotsdam.cs.groupplaner.datefinder.service;

import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStream;
import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStreamDateCreator;
import de.unipotsdam.cs.groupplaner.datefinder.list.DateCombiner;
import de.unipotsdam.cs.groupplaner.datefinder.service.modifier.NightDatePrioritizeModifier;
import de.unipotsdam.cs.groupplaner.datefinder.service.modifier.OptimalDatePrioritizeModifier;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrioritizeDatesService {

	@Autowired
	private DateCombiner<PrioritizedDate> dateCombiner;
	@Autowired
	private ConsecutiveDateStreamDateCreator<PrioritizedDate> consecutiveDateStreamDateCreator;

	@Autowired
	private OptimalDatePrioritizeModifier optimalDatePrioritizeModifier;
	@Autowired
	private NightDatePrioritizeModifier nightDatePrioritizeModifier;

	public List<PrioritizedDate> prioritizeDates(final ConsecutiveDateStream<TraitDate> dates) {
		ConsecutiveDateStream<PrioritizedDate> prioritizedDates = basePrioritizeDates(dates);

		prioritizedDates.modifyList(optimalDatePrioritizeModifier);
		prioritizedDates.modifyList(nightDatePrioritizeModifier);

		return Lists.newArrayList(prioritizedDates.getDates());
	}

	/**
	 * Converts a LinearDateList of TraitDates to a LinearDateList of PrioritizedDates.
	 * Blocked dates get PRIORITY_BLOCKED all others get PRIORITY_NEUTRAL.
	 */
	private ConsecutiveDateStream<PrioritizedDate> basePrioritizeDates(final ConsecutiveDateStream<TraitDate> dates) {
		final ConsecutiveDateStream<PrioritizedDate> prioritizedDates = new ConsecutiveDateStream<PrioritizedDate>(dateCombiner, consecutiveDateStreamDateCreator);
		for (TraitDate date : dates.getDates()) {
			if (date.hasTrait(TraitDate.TRAIT_BLOCKED_DATE)) {
				final PrioritizedDate newDate = new PrioritizedDate(date, PrioritizedDate.PRIORITY_BLOCKED);
				prioritizedDates.add(newDate);
			} else {
				final PrioritizedDate newDate = new PrioritizedDate(date, PrioritizedDate.PRIORITY_NEUTRAL);
				prioritizedDates.add(newDate);
			}
		}
		return prioritizedDates;
	}

}
