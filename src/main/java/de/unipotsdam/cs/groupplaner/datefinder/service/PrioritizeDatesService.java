package de.unipotsdam.cs.groupplaner.datefinder.service;

import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStream;
import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStreamDateCreator;
import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStreamModifier;
import de.unipotsdam.cs.groupplaner.datefinder.list.DateCombiner;
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
	private List<ConsecutiveDateStreamModifier<PrioritizedDate>> listModifiers;

	public List<PrioritizedDate> prioritizeDates(final ConsecutiveDateStream<TraitDate> dates) {
		ConsecutiveDateStream<PrioritizedDate> prioritizedDates = basePrioritizeDates(dates);

		for (ConsecutiveDateStreamModifier<PrioritizedDate> listModifier : listModifiers) {
			prioritizedDates.modifyList(listModifier);
		}

		return Lists.newArrayList(prioritizedDates.getDates());
	}

	/**
	 * Converts a LinearDateList of TraitDates to a LinearDateList of PrioritizedDates.
	 * Blocked dates get PRIORITY_BLOCKED all others get PRIORITY_NEUTRAL.
	 */
	private ConsecutiveDateStream<PrioritizedDate> basePrioritizeDates(final ConsecutiveDateStream<TraitDate> dates) {
		final ConsecutiveDateStream<PrioritizedDate> prioritizedDates = new ConsecutiveDateStream<PrioritizedDate>(dateCombiner, consecutiveDateStreamDateCreator);
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
