package de.unipotsdam.cs.groupplaner.datefinder.service.modifier;

import com.google.common.collect.Lists;
import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStreamModifier;
import de.unipotsdam.cs.groupplaner.domain.PeriodDate;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Reduces the priority of all dates in the list between 8pm and 8am by 3.
 */
@Component
public class NightDatePrioritizeModifier implements ConsecutiveDateStreamModifier<PrioritizedDate> {

	public static final int NIGHT_BEGIN = 20;
	public static final int NIGHT_END = 8;

	public static final int MINUTES_PER_HOUR = 60;
	public static final int MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR;

	public static final int NIGHT_DATE_PRIORITY_MALUS = 3;

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

}
