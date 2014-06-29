package de.unipotsdam.cs.groupplaner.service.impl;

import de.unipotsdam.cs.groupplaner.domain.PeriodDate;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class PrioritizeDatesService {

	public List<PrioritizedDate> prioritizeDates(final List<PeriodDate> allBlockedDates, final List<PeriodDate> availableDates) {
		final List<PrioritizedDate> prioritizedDates = new ArrayList<PrioritizedDate>();
		for (PeriodDate date : allBlockedDates) {
			prioritizedDates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_BLOCKED));
		}
		for (PeriodDate date : availableDates) {
			prioritizedDates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_OPTIMAL));
		}
		Collections.sort(prioritizedDates, new Comparator<PeriodDate>() {
			@Override
			public int compare(PeriodDate date1, PeriodDate date2) {
				return date1.getStart().compareTo(date2.getStart());
			}
		});
		return prioritizedDates;
	}


	/*

	Fuer Qualitaetsbewertung:
	- Ueberlappungen von BlockedDates zaehlen
	- Nachttermine abwerten
	- Termine nahe an BlockedDates mit hoher Ueberlappung aufwerten

	 */

}
