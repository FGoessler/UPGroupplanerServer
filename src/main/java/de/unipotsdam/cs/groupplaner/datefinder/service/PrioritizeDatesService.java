package de.unipotsdam.cs.groupplaner.datefinder.service;

import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.datefinder.list.LinearDateList;
import de.unipotsdam.cs.groupplaner.datefinder.list.LinearDateListDateCreator;
import de.unipotsdam.cs.groupplaner.datefinder.list.LinearDateListModifier;
import de.unipotsdam.cs.groupplaner.datefinder.list.TraitDateCombiner;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrioritizeDatesService {

	@Autowired
	private TraitDateCombiner<PrioritizedDate> traitDateCombiner;
	@Autowired
	private LinearDateListDateCreator<PrioritizedDate> linearDateListDateCreator;
	@Autowired
	private List<LinearDateListModifier<PrioritizedDate>> listModifiers;

	public List<PrioritizedDate> prioritizeDates(final LinearDateList<TraitDate> dates) {
		LinearDateList<PrioritizedDate> prioritizedDates = basePrioritizeDates(dates);

		for (LinearDateListModifier<PrioritizedDate> listModifier : listModifiers) {
			prioritizedDates.modifyList(listModifier);
		}

		return Lists.newArrayList(prioritizedDates.getDates());
	}

	/**
	 * Converts a LinearDateList of TraitDates to a LinearDateList of PrioritizedDates.
	 * Blocked dates get PRIORITY_BLOCKED all others get PRIORITY_NEUTRAL.
	 */
	private LinearDateList<PrioritizedDate> basePrioritizeDates(final LinearDateList<TraitDate> dates) {
		final LinearDateList<PrioritizedDate> prioritizedDates = new LinearDateList<PrioritizedDate>(traitDateCombiner, linearDateListDateCreator);
		for (TraitDate date : dates.getDates()) {
			if (date.hasTrait(TraitDate.TRAIT_BLOCKED_DATE) || date.hasTrait(TraitDate.TRAIT_ACCEPTED_DATE)) {
				final PrioritizedDate newDate = new PrioritizedDate(date, PrioritizedDate.PRIORITY_BLOCKED);
				prioritizedDates.add(newDate);
			} else {
				final PrioritizedDate newDate = new PrioritizedDate(date, PrioritizedDate.PRIORITY_NEUTRAL);
				prioritizedDates.add(newDate);
			}
		}
		return prioritizedDates;
	}

	/*

	Fuer Qualitaetsbewertung:
	- Ueberlappungen von BlockedDates zaehlen -> erfordert erweiterten BlockedDates Datentyp und AggregationService

	Idee:
	BlockedDates erhalten mehr negative Prio je mehr Mitglieder dort blocked sind.
	Verfügbare Termine erhalten höhere Prio je negativer die Prio naher BlockedDates ist.

	 */

}
