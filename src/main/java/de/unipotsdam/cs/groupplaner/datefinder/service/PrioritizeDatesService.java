package de.unipotsdam.cs.groupplaner.datefinder.service;

import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.datefinder.list.LinearDateList;
import de.unipotsdam.cs.groupplaner.datefinder.list.LinearDateListDateCreator;
import de.unipotsdam.cs.groupplaner.datefinder.list.LinearDateListModifier;
import de.unipotsdam.cs.groupplaner.datefinder.list.TraitDateCombiner;
import de.unipotsdam.cs.groupplaner.domain.PeriodDate;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrioritizeDatesService {

	public static final int MAX_OPTIMAL_DATE_DURATION = 60;
	public static final int MIN_OPTIMAL_DATE_DURATION = 30;

	public static final int NIGHT_BEGIN = 20;
	public static final int NIGHT_END = 8;

	public static final int MINUTES_PER_HOUR = 60;
	public static final int MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR;

	public static final int NIGHT_DATE_PRIORITY_MALUS = 3;

	@Autowired
	private TraitDateCombiner<PrioritizedDate> traitDateCombiner;
	@Autowired
	private LinearDateListDateCreator<PrioritizedDate> linearDateListDateCreator;

	// TODO: inject the list of available LinearDateListModifier classes and apply all of them => greater flexibility

	public List<PrioritizedDate> prioritizeDates(final LinearDateList<TraitDate> dates) {
		LinearDateList<PrioritizedDate> prioritizedDates = basePrioritizeDates(dates);

		prioritizedDates.modifyList(analyzeDatesModifier);

		prioritizedDates.modifyList(nightDateModifier);

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

	/**
	 * Convert neutral dates near blocked dates to optimal dates depending on min and max optimal date duration.
	 */
	private LinearDateListModifier<PrioritizedDate> analyzeDatesModifier = new LinearDateListModifier<PrioritizedDate>() {
		@Override
		public List<PrioritizedDate> modifyDate(final PrioritizedDate predecessorDate, final PrioritizedDate curDate, final PrioritizedDate successorDate) {
			if (curDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED) {
				return Lists.newArrayList(curDate);
			} else {
				final List<PrioritizedDate> dates = new ArrayList<PrioritizedDate>();
				final Integer duration = curDate.getDuration();

				// TODO: work with priority deltas instead of fixed values

				// date is between two blocked dates
				if (predecessorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED && successorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED) {
					if (duration > MAX_OPTIMAL_DATE_DURATION * 2) {    // two optimal dates fit in with break between
						dates.add(new PrioritizedDate(curDate.getStart(), curDate.getStart() + MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_OPTIMAL));
						dates.add(new PrioritizedDate(curDate.getStart() + MAX_OPTIMAL_DATE_DURATION, curDate.getEnd() - MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_NEUTRAL));
						dates.add(new PrioritizedDate(curDate.getEnd() - MAX_OPTIMAL_DATE_DURATION, curDate.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
					} else if (duration > MAX_OPTIMAL_DATE_DURATION && duration <= MAX_OPTIMAL_DATE_DURATION * 2) {    // two optimal dates fit in without break between
						int middle = curDate.getStart() + (int) Math.floor(duration / 2);
						dates.add(new PrioritizedDate(curDate.getStart(), middle, PrioritizedDate.PRIORITY_OPTIMAL));
						dates.add(new PrioritizedDate(middle, curDate.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
					} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {    // one optimal date fits in
						dates.add(new PrioritizedDate(curDate, PrioritizedDate.PRIORITY_OPTIMAL));
					} else {        // no optimal date fits in
						dates.add(new PrioritizedDate(curDate, PrioritizedDate.PRIORITY_NEUTRAL));
					}
					// date is before a blocked date
				} else if (predecessorDate.getPriority() == PrioritizedDate.PRIORITY_NEUTRAL && successorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED) {
					if (duration > MAX_OPTIMAL_DATE_DURATION) {                // one optimal fits in with time before
						dates.add(new PrioritizedDate(curDate.getStart(), curDate.getEnd() - MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_NEUTRAL));
						dates.add(new PrioritizedDate(curDate.getEnd() - MAX_OPTIMAL_DATE_DURATION, curDate.getEnd(), PrioritizedDate.PRIORITY_OPTIMAL));
					} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {        // one optimal fits in without time before
						dates.add(new PrioritizedDate(curDate, PrioritizedDate.PRIORITY_OPTIMAL));
					} else {        // no optimal date fits in
						dates.add(new PrioritizedDate(curDate, PrioritizedDate.PRIORITY_NEUTRAL));
					}
					// date is after a blocked date
				} else if (predecessorDate.getPriority() == PrioritizedDate.PRIORITY_BLOCKED && successorDate.getPriority() == PrioritizedDate.PRIORITY_NEUTRAL) {
					if (duration > MAX_OPTIMAL_DATE_DURATION) {                // one optimal fits in with time after
						dates.add(new PrioritizedDate(curDate.getStart(), curDate.getStart() + MAX_OPTIMAL_DATE_DURATION, PrioritizedDate.PRIORITY_OPTIMAL));
						dates.add(new PrioritizedDate(curDate.getStart() + MAX_OPTIMAL_DATE_DURATION, curDate.getEnd(), PrioritizedDate.PRIORITY_NEUTRAL));
					} else if (duration >= MIN_OPTIMAL_DATE_DURATION) {        // one optimal fits in without time after
						dates.add(new PrioritizedDate(curDate, PrioritizedDate.PRIORITY_OPTIMAL));
					} else {        // no optimal date fits in
						dates.add(new PrioritizedDate(curDate, PrioritizedDate.PRIORITY_NEUTRAL));
					}
				}
				return dates;
			}
		}
	};

	/**
	 * Reduces the priority of all dates in the list between 8pm and 8am by 3.
	 */
	private LinearDateListModifier<PrioritizedDate> nightDateModifier = new LinearDateListModifier<PrioritizedDate>() {
		@Override
		public List<PrioritizedDate> modifyDate(PrioritizedDate prevDate, PrioritizedDate curDate, PrioritizedDate nextDate) {
			if (isNightDate(curDate)) {
				return splitAndMarkNightDate(curDate);
			} else {
				return Lists.newArrayList(curDate);
			}
		}

		private boolean isNightDate(final PeriodDate date) {
			return date.getStartHour() >= NIGHT_BEGIN || date.getStartHour() < NIGHT_END ||
					date.getEndHour() >= NIGHT_BEGIN || date.getEndHour() <= NIGHT_END ||
					date.getEndWeekday() > date.getStartWeekday();
		}

		/**
		 * Splits a given date into several dates with different priorities depending on whether they are at night or not.
		 * It uses a recursive approach by cutting of the beginning of the date and calling itself with the rest of the date.
		 */
		private List<PrioritizedDate> splitAndMarkNightDate(final PrioritizedDate date) {
			final List<PrioritizedDate> resultingDates = Lists.newArrayList();

			final int cutPoint_8am = date.getStartWeekday() * MINUTES_PER_DAY + NIGHT_END * MINUTES_PER_HOUR;
			final int cutPoint_8pm = date.getStartWeekday() * MINUTES_PER_DAY + NIGHT_BEGIN * MINUTES_PER_HOUR;
			final int cutPoint_8amNextDay = (date.getStartWeekday() + 1) * MINUTES_PER_DAY + NIGHT_END * MINUTES_PER_HOUR;

			// cut of part starting between 0am and 8am and ending after 8am
			if (date.getStartHour() < NIGHT_END && date.getEnd() > cutPoint_8am) {
				resultingDates.add(new PrioritizedDate(date.getStart(), cutPoint_8am, date.getPriority() - NIGHT_DATE_PRIORITY_MALUS));
				resultingDates.addAll(splitAndMarkNightDate(new PrioritizedDate(cutPoint_8am, date.getEnd(), date.getPriority())));
				// cut of part starting between 8am and 8pm and ending after 8pm
			} else if (date.getStartHour() < NIGHT_BEGIN && date.getEnd() > cutPoint_8pm) {
				resultingDates.add(new PrioritizedDate(date.getStart(), cutPoint_8pm, date.getPriority()));
				resultingDates.addAll(splitAndMarkNightDate(new PrioritizedDate(cutPoint_8pm, date.getEnd(), date.getPriority())));
				// cut of part starting after 8pm and ending after 8am of the next day
			} else if (date.getStartHour() >= NIGHT_BEGIN && date.getEnd() > cutPoint_8amNextDay) {
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

			return resultingDates;
		}
	};

	/*

	Fuer Qualitaetsbewertung:
	- Ueberlappungen von BlockedDates zaehlen -> erfordert erweiterten BlockedDates Datentyp und AggregationService

	Idee:
	BlockedDates erhalten mehr negative Prio je mehr Mitglieder dort blocked sind.
	Verfügbare Termine erhalten höhere Prio je negativer die Prio naher BlockedDates ist.

	 */

}
