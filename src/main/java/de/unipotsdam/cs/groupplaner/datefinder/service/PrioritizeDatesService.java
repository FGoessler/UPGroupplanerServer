package de.unipotsdam.cs.groupplaner.datefinder.service;

import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.datefinder.list.LinearDateList;
import de.unipotsdam.cs.groupplaner.domain.PeriodDate;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class PrioritizeDatesService {

	public static final int MAX_OPTIMAL_DATE_DURATION = 60;
	public static final int MIN_OPTIMAL_DATE_DURATION = 30;

	public static final int EIGHT_PM = 20;
	public static final int EIGHT_AM = 8;

	public static final int MINUTES_PER_HOUR = 60;
	public static final int MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR;

	public static final int NIGHT_DATE_PRIORITY_MALUS = 3;

	public List<PrioritizedDate> prioritizeDates(final LinearDateList dates) {
		List<PrioritizedDate> prioritizedDates = basePrioritizeDates(dates);

		int currentIndex = 0;
		while (currentIndex < prioritizedDates.size()) {
			final PrioritizedDate predecessorDate = getPredecessor(prioritizedDates, currentIndex);
			final PrioritizedDate successorDate = getSuccessor(prioritizedDates, currentIndex);

			final List<PrioritizedDate> analysisResultedDates = analyzeDate(predecessorDate, prioritizedDates.get(currentIndex), successorDate);

			prioritizedDates.remove(currentIndex);
			prioritizedDates.addAll(currentIndex, analysisResultedDates);

			currentIndex += analysisResultedDates.size();
		}

		prioritizedDates = downVoteNightDates(prioritizedDates);

		return prioritizedDates;
	}

	/**
	 * Combine all dates into one list. Blocked dates get PRIORITY_BLOCKED all others get PRIORITY_NEUTRAL.
	 */
	private List<PrioritizedDate> basePrioritizeDates(final LinearDateList dates) {
		final List<PrioritizedDate> prioritizedDates = new ArrayList<PrioritizedDate>();
		for (TraitDate date : dates.getDates()) {
			if (date.hasTrait(TraitDate.TRAIT_BLOCKED_DATE) || date.hasTrait(TraitDate.TRAIT_ACCEPTED_DATE)) {
				prioritizedDates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_BLOCKED));
			} else {
				prioritizedDates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_NEUTRAL));
			}
		}
		return prioritizedDates;
	}

	/**
	 * Convert neutral dates near blocked dates to optimal dates depending on min and max optimal date duration.
	 */
	private List<PrioritizedDate> analyzeDate(final PrioritizedDate predecessorDate, final PrioritizedDate date, final PrioritizedDate successorDate) {
		if (date.getPriority() == PrioritizedDate.PRIORITY_BLOCKED) {
			return Lists.newArrayList(date);
		} else {
			final List<PrioritizedDate> dates = new ArrayList<PrioritizedDate>();
			final Integer duration = date.getDuration();

			// date is between two blocked dates
			if (predecessorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED && successorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED) {
				if (duration > MAX_OPTIMAL_DATE_DURATION * 2) {	// two optimal dates fit in with break between
					dates.add(new PrioritizedDate(date.getStart(), date.getStart() + MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_OPTIMAL));
					dates.add(new PrioritizedDate(date.getStart() + MAX_OPTIMAL_DATE_DURATION, date.getEnd() - MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_NEUTRAL));
					dates.add(new PrioritizedDate(date.getEnd() - MAX_OPTIMAL_DATE_DURATION, date.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
				} else if (duration > MAX_OPTIMAL_DATE_DURATION && duration <= MAX_OPTIMAL_DATE_DURATION * 2) {	// two optimal dates fit in without break between
					int middle = date.getStart() + (int) Math.floor(duration / 2);
					dates.add(new PrioritizedDate(date.getStart(), middle, PrioritizedDate.PRIORITY_OPTIMAL));
					dates.add(new PrioritizedDate(middle, date.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
				} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {	// one optimal date fits in
					dates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_OPTIMAL));
				} else {		// no optimal date fits in
					dates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_NEUTRAL));
				}
			// date is before a blocked date
			} else if (predecessorDate.getPriority() == PrioritizedDate.PRIORITY_NEUTRAL && successorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED) {
				if (duration > MAX_OPTIMAL_DATE_DURATION) {				// one optimal fits in with time before
					dates.add(new PrioritizedDate(date.getStart(), date.getEnd() - MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_NEUTRAL));
					dates.add(new PrioritizedDate(date.getEnd() - MAX_OPTIMAL_DATE_DURATION, date.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
				} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {		// one optimal fits in without time before
					dates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_OPTIMAL));
				} else {		// no optimal date fits in
					dates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_NEUTRAL));
				}
			// date is after a blocked date
			} else if (predecessorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED && successorDate.getPriority() == PrioritizedDate.PRIORITY_NEUTRAL) {
				if (duration > MAX_OPTIMAL_DATE_DURATION) {				// one optimal fits in with time after
					dates.add(new PrioritizedDate(date.getStart(), date.getStart() + MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_OPTIMAL));
					dates.add(new PrioritizedDate(date.getStart() + MAX_OPTIMAL_DATE_DURATION, date.getEnd(), PrioritizedDate.PRIORITY_NEUTRAL));
				} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {		// one optimal fits in without time after
					dates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_OPTIMAL));
				} else {		// no optimal date fits in
					dates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_NEUTRAL));
				}
			}
			return dates;
		}
	}

	/**
	 * Reduces the priority of all dates in the list between 8pm and 8am by 3.
	 */
	private List<PrioritizedDate> downVoteNightDates(List<PrioritizedDate> allDates) {
		int currentIndex = 0;
		while (currentIndex < allDates.size()) {
			final PrioritizedDate date = allDates.get(currentIndex);

			if (isNightDate(date)) {
				final List<PrioritizedDate> analysisResultedDates = splitAndMarkNightDate(date);
				allDates.remove(currentIndex);
				allDates.addAll(currentIndex, analysisResultedDates);
				currentIndex += analysisResultedDates.size();
			} else {
				currentIndex++;
			}
		}

		return allDates;
	}

	private boolean isNightDate(final PeriodDate date) {
		return date.getStartHour() >= EIGHT_PM ||
				date.getStartHour() < EIGHT_AM ||
				date.getEndHour() >= EIGHT_PM ||
				date.getEndHour() <= EIGHT_AM ||
				date.getEndWeekday() > date.getStartWeekday();
	}

	/**
	 * Splits a given date into several dates with different priorities depending on whether they are at night or not.
	 * It uses a recursive approach by cutting of the beginning of the date and calling itself with the rest of the date.
	 */
	List<PrioritizedDate> splitAndMarkNightDate(final PrioritizedDate date) {
		final List<PrioritizedDate> resultingDates = Lists.newArrayList();

		final int cutPoint_8am = date.getStartWeekday() * MINUTES_PER_DAY + EIGHT_AM * MINUTES_PER_HOUR;
		final int cutPoint_8pm = date.getStartWeekday() * MINUTES_PER_DAY + EIGHT_PM * MINUTES_PER_HOUR;
		final int cutPoint_8amNextDay = (date.getStartWeekday() + 1) * MINUTES_PER_DAY + EIGHT_AM * MINUTES_PER_HOUR;

		// cut of part starting between 0am and 8am and ending after 8am
		if (date.getStartHour() < EIGHT_AM && date.getEnd() > cutPoint_8am) {
			resultingDates.add(new PrioritizedDate(date.getStart(), cutPoint_8am, date.getPriority() - NIGHT_DATE_PRIORITY_MALUS));
			resultingDates.addAll(splitAndMarkNightDate(new PrioritizedDate(cutPoint_8am, date.getEnd(), date.getPriority())));
		// cut of part starting between 8am and 8pm and ending after 8pm
		} else if (date.getStartHour() < EIGHT_PM && date.getEnd() > cutPoint_8pm) {
			resultingDates.add(new PrioritizedDate(date.getStart(), cutPoint_8pm, date.getPriority()));
			resultingDates.addAll(splitAndMarkNightDate(new PrioritizedDate(cutPoint_8pm, date.getEnd(), date.getPriority())));
		// cut of part starting after 8pm and ending after 8am of the next day
		} else if (date.getStartHour() >= EIGHT_PM && date.getEnd() > cutPoint_8amNextDay) {
			resultingDates.add(new PrioritizedDate(date.getStart(), cutPoint_8amNextDay, date.getPriority() - NIGHT_DATE_PRIORITY_MALUS));
			resultingDates.addAll(splitAndMarkNightDate(new PrioritizedDate(cutPoint_8amNextDay, date.getEnd(), date.getPriority())));
		// any other date does not overlap a 8am or 8pm border and doesn't need to be split any further
		} else {
			if (isNightDate(date)) {
				resultingDates.add(new PrioritizedDate(date, date.getPriority() - NIGHT_DATE_PRIORITY_MALUS));
			} else {
				resultingDates.add(date);
			}
		}

		Collections.sort(resultingDates, new Comparator<PeriodDate>() {
			@Override
			public int compare(PeriodDate date1, PeriodDate date2) {
				return date1.getStart().compareTo(date2.getStart());
			}
		});

		return resultingDates;
	}

	private PrioritizedDate getPredecessor(final List<PrioritizedDate> prioritizedDates, final int currentIndex) {
		PrioritizedDate predecessorDate;
		if (currentIndex == 0) {
			predecessorDate = prioritizedDates.get(prioritizedDates.size() - 1);
		} else {
			predecessorDate = prioritizedDates.get(currentIndex - 1);
		}
		return predecessorDate;
	}

	private PrioritizedDate getSuccessor(final List<PrioritizedDate> prioritizedDates, final int currentIndex) {
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
	- Ueberlappungen von BlockedDates zaehlen -> erfordert erweiterten BlockedDates Datentyp und AggregationService

	Idee:
	BlockedDates erhalten mehr negative Prio je mehr Mitglieder dort blocked sind.
	Verfügbare Termine erhalten höhere Prio je negativer die Prio naher BlockedDates ist.

	 */

}
