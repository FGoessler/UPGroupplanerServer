package de.unipotsdam.cs.groupplaner.datefinder.service;

import com.google.common.collect.Lists;
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
			prioritizedDates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_NEUTRAL));
		}
		Collections.sort(prioritizedDates, new Comparator<PeriodDate>() {
			@Override
			public int compare(PeriodDate date1, PeriodDate date2) {
				return date1.getStart().compareTo(date2.getStart());
			}
		});


		int currentIndex = 0;
		while (currentIndex < prioritizedDates.size()) {
			final PrioritizedDate predecessorDate = getPredecessor(prioritizedDates, currentIndex);
			final PrioritizedDate successorDate = getSuccessor(prioritizedDates, currentIndex);

			final List<PrioritizedDate> analysisResultedDates = analyzeDate(predecessorDate, prioritizedDates.get(currentIndex), successorDate);

			prioritizedDates.remove(currentIndex);
			prioritizedDates.addAll(currentIndex, analysisResultedDates);

			currentIndex += analysisResultedDates.size();
		}


		return prioritizedDates;
	}

	// Convert neutral dates near blocked dates to optimal dates.
	private List<PrioritizedDate> analyzeDate(final PrioritizedDate predecessorDate, final PrioritizedDate date, final PrioritizedDate successorDate) {
		if (date.getPriority() == PrioritizedDate.PRIORITY_BLOCKED) {
			return Lists.newArrayList(date);
		} else {
			final List<PrioritizedDate> dates = new ArrayList<PrioritizedDate>();
			final Integer duration = date.getDuration();
			if (predecessorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED && successorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED) {
				if (duration > 120) {
					dates.add(new PrioritizedDate(date.getStart(), date.getStart() + 60, PrioritizedDate.PRIORITY_OPTIMAL));
					dates.add(new PrioritizedDate(date.getStart() + 60, date.getEnd() - 60, PrioritizedDate.PRIORITY_NEUTRAL));
					dates.add(new PrioritizedDate(date.getEnd() - 60, date.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
				} else if (duration > 60 && duration <= 120) {
					int middle = date.getStart() + (int) Math.floor(duration / 2);
					dates.add(new PrioritizedDate(date.getStart(), middle, PrioritizedDate.PRIORITY_OPTIMAL));
					dates.add(new PrioritizedDate(middle, date.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
				} else if (duration >= 30) {
					dates.add(new PrioritizedDate(date.getStart(), date.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
				} else {
					dates.add(new PrioritizedDate(date.getStart(), date.getEnd(), PrioritizedDate.PRIORITY_NEUTRAL));
				}
			} else if (predecessorDate.getPriority() == PrioritizedDate.PRIORITY_NEUTRAL && successorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED) {
				if (duration > 60) {
					dates.add(new PrioritizedDate(date.getStart(), date.getEnd() - 60, PrioritizedDate.PRIORITY_NEUTRAL));
					dates.add(new PrioritizedDate(date.getEnd() - 60, date.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
				} else if (duration >= 30) {
					dates.add(new PrioritizedDate(date.getStart(), date.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
				} else {
					dates.add(new PrioritizedDate(date.getStart(), date.getEnd(), PrioritizedDate.PRIORITY_NEUTRAL));
				}
			} else if (predecessorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED && successorDate.getPriority() == PrioritizedDate.PRIORITY_NEUTRAL) {
				if (duration > 60) {
					dates.add(new PrioritizedDate(date.getStart(), date.getStart() + 60, PrioritizedDate.PRIORITY_OPTIMAL));
					dates.add(new PrioritizedDate(date.getStart() + 60, date.getEnd(), PrioritizedDate.PRIORITY_NEUTRAL));
				} else if (duration >= 30) {
					dates.add(new PrioritizedDate(date.getStart(), date.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
				} else {
					dates.add(new PrioritizedDate(date.getStart(), date.getEnd(), PrioritizedDate.PRIORITY_NEUTRAL));
				}
			}
			return dates;
		}
	}

	private PrioritizedDate getPredecessor(List<PrioritizedDate> prioritizedDates, int currentIndex) {
		PrioritizedDate predecessorDate;
		if (currentIndex == 0) {
			predecessorDate = prioritizedDates.get(prioritizedDates.size() - 1);
		} else {
			predecessorDate = prioritizedDates.get(currentIndex - 1);
		}
		return predecessorDate;
	}

	private PrioritizedDate getSuccessor(List<PrioritizedDate> prioritizedDates, int currentIndex) {
		PrioritizedDate successorDate;
		if (currentIndex == prioritizedDates.size() - 1) {
			successorDate = prioritizedDates.get(0);
		} else {
			successorDate = prioritizedDates.get(currentIndex + 1);
		}
		return successorDate;
	}


	/*

	Fuer Qualitaetsbewertung:
	- Nachttermine abwerten -> in Schleife alle Dates zwischen 20 und 8 Uhr mit -3 abwerten (Dates gegebenenfalls splitten)
	- Ueberlappungen von BlockedDates zaehlen -> erfordert erweiterten BlockedDates Datentyp und AggregationService

	Idee:
	BlockedDates erhalten mehr negative Prio je mehr Mitglieder dort blocked sind.
	Verfügbare Termine erhalten höhere Prio je negativer die Prio naher BlockedDates ist.

	 */

}
