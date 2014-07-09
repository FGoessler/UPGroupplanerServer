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

	public static final int MAX_OPTIMAL_DATE_DURATION = 60;
	public static final int MIN_OPTIMAL_DATE_DURATION = 30;
	public static final int EIGHT_PM = 20;
	public static final int EIGHT_AM = 8;
	public static final int MINUTES_PER_HOUR = 60;
	public static final int MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR;
	public static final int NIGHT_DATE_PRIORITY_MALUS = 3;

	public List<PrioritizedDate> prioritizeDates(final List<PeriodDate> allBlockedDates, final List<PeriodDate> availableDates) {
		List<PrioritizedDate> prioritizedDates = combineSortAndBasePrioritizeDates(allBlockedDates, availableDates);

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
	private List<PrioritizedDate> combineSortAndBasePrioritizeDates(List<PeriodDate> allBlockedDates, List<PeriodDate> availableDates) {
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
			if (predecessorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED && successorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED) {
				if (duration > MAX_OPTIMAL_DATE_DURATION * 2) {
					dates.add(new PrioritizedDate(date.getStart(), date.getStart() + MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_OPTIMAL));
					dates.add(new PrioritizedDate(date.getStart() + MAX_OPTIMAL_DATE_DURATION, date.getEnd() - MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_NEUTRAL));
					dates.add(new PrioritizedDate(date.getEnd() - MAX_OPTIMAL_DATE_DURATION, date.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
				} else if (duration > MAX_OPTIMAL_DATE_DURATION && duration <= MAX_OPTIMAL_DATE_DURATION * 2) {
					int middle = date.getStart() + (int) Math.floor(duration / 2);
					dates.add(new PrioritizedDate(date.getStart(), middle, PrioritizedDate.PRIORITY_OPTIMAL));
					dates.add(new PrioritizedDate(middle, date.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
				} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {
					dates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_OPTIMAL));
				} else {
					dates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_NEUTRAL));
				}
			} else if (predecessorDate.getPriority() == PrioritizedDate.PRIORITY_NEUTRAL && successorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED) {
				if (duration > MAX_OPTIMAL_DATE_DURATION) {
					dates.add(new PrioritizedDate(date.getStart(), date.getEnd() - MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_NEUTRAL));
					dates.add(new PrioritizedDate(date.getEnd() - MAX_OPTIMAL_DATE_DURATION, date.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
				} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {
					dates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_OPTIMAL));
				} else {
					dates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_NEUTRAL));
				}
			} else if (predecessorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED && successorDate.getPriority() == PrioritizedDate.PRIORITY_NEUTRAL) {
				if (duration > MAX_OPTIMAL_DATE_DURATION) {
					dates.add(new PrioritizedDate(date.getStart(), date.getStart() + MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_OPTIMAL));
					dates.add(new PrioritizedDate(date.getStart() + MAX_OPTIMAL_DATE_DURATION, date.getEnd(), PrioritizedDate.PRIORITY_NEUTRAL));
				} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {
					dates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_OPTIMAL));
				} else {
					dates.add(new PrioritizedDate(date, PrioritizedDate.PRIORITY_NEUTRAL));
				}
			}
			return dates;
		}
	}

	/**
	 * Reduce priority of all dates between 8pm and 8am by 3.
	 */
	private List<PrioritizedDate> downVoteNightDates(List<PrioritizedDate> allDates) {
		int currentIndex = 0;
		while (currentIndex < allDates.size()) {
			final PrioritizedDate date = allDates.get(currentIndex);

			if (isNightDate(date)) {
				final List<PrioritizedDate> analysisResultedDates = splitAndMarkNightDate(date, date.getPriority());
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

	List<PrioritizedDate> splitAndMarkNightDate(final PrioritizedDate date, final Integer origDatePrio) {
		List<PrioritizedDate> resultingDates = Lists.newArrayList();

		if (date.getStartHour() < EIGHT_AM) {
			final int cutPoint = date.getEndWeekday() * MINUTES_PER_DAY + EIGHT_AM * MINUTES_PER_HOUR;
			PrioritizedDate prevDate = new PrioritizedDate(date.getStart(), cutPoint, origDatePrio - NIGHT_DATE_PRIORITY_MALUS);
			resultingDates.add(prevDate);

			final List<PrioritizedDate> dates = splitAndMarkNightDate(new PrioritizedDate(cutPoint, date.getEnd(), origDatePrio), origDatePrio);
			resultingDates.addAll(dates);
		} else if (date.getStartHour() < EIGHT_PM && date.getEnd() > (date.getStartWeekday() * MINUTES_PER_DAY + EIGHT_PM * MINUTES_PER_HOUR)) {
			final int cutPoint = date.getStartWeekday() * MINUTES_PER_DAY + EIGHT_PM * MINUTES_PER_HOUR;
			PrioritizedDate prevDate = new PrioritizedDate(date.getStart(), cutPoint, origDatePrio);
			resultingDates.add(prevDate);

			final List<PrioritizedDate> dates = splitAndMarkNightDate(new PrioritizedDate(cutPoint, date.getEnd(), origDatePrio), origDatePrio);
			resultingDates.addAll(dates);
		} else if (date.getStartHour() >= EIGHT_PM && ((date.getEndHour() >= EIGHT_AM && date.getEndWeekday() > date.getStartWeekday()) || date.getEndWeekday() > date.getStartWeekday() + 1)) {
			final int cutPoint = (date.getStartWeekday() + 1) * MINUTES_PER_DAY + EIGHT_AM * MINUTES_PER_HOUR;
			PrioritizedDate prevDate = new PrioritizedDate(date.getStart(), cutPoint, origDatePrio - NIGHT_DATE_PRIORITY_MALUS);
			resultingDates.add(prevDate);

			final List<PrioritizedDate> dates = splitAndMarkNightDate(new PrioritizedDate(cutPoint, date.getEnd(), origDatePrio), origDatePrio);
			resultingDates.addAll(dates);
		} else {
			if (isNightDate(date)) {
				resultingDates.add(new PrioritizedDate(date, origDatePrio - NIGHT_DATE_PRIORITY_MALUS));
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
