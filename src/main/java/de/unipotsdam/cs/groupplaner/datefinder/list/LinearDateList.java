package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.PeriodDate;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LinearDateList {

	final private TreeMap<Integer, TraitDate> dates;

	final private TraitCombiner traitCombiner;

	public LinearDateList(final TraitCombiner traitCombiner) {
		this.traitCombiner = traitCombiner;

		dates = new TreeMap<Integer, TraitDate>();
		dates.put(PeriodDate.START_OF_WEEK, new TraitDate(PeriodDate.START_OF_WEEK, PeriodDate.END_OF_WEEK));
	}

	public void add(final TraitDate date) {
		// split dates that overflow at the end of the week
		if (date.getEnd() < date.getStart()) {
			add(new TraitDate(PeriodDate.START_OF_WEEK, date.getEnd(), date.getTraits()));
			add(new TraitDate(date.getStart(), PeriodDate.END_OF_WEEK, date.getTraits()));
			return;
		}

		final Map.Entry<Integer, TraitDate> prevDateEntry = dates.floorEntry(date.getStart());
		final TraitDate prevDate = prevDateEntry.getValue();
		final Integer prevKey = prevDateEntry.getKey();

		if (date.getEnd() <= prevDate.getEnd()) {    // new date is fully contained in prevDate
			if (!traitCombiner.areTraitsEqual(prevDate, date)) {
				// only create a date before the new date if doesn't start at the same time as the previous date
				if (prevDate.getStart() < date.getStart()) putDate(prevKey, date.getStart(), prevDate.getTraits());

				// create the date for the overlapping period with combined traits
				putDate(date.getStart(), date.getEnd(), traitCombiner.combineTraits(prevDate, date));

				// only create a date after the new date if doesn't end at the same time as the previous date
				if (date.getEnd() < prevDate.getEnd()) putDate(date.getEnd(), prevDate.getEnd(), prevDate.getTraits());
			}
			// we don't need to do anything when the traits are equal cause then the new date is already equally represented by the existing one
		} else {                                    // new date overlaps to later dates
			if (!traitCombiner.areTraitsEqual(prevDate, date)) {
				// only create a date before the new date if doesn't start at the same time as the previous date
				if (prevDate.getStart() < date.getStart()) putDate(prevKey, date.getStart(), prevDate.getTraits());

				// create the date for the overlapping period with combined traits
				putDate(date.getStart(), prevDate.getEnd(), traitCombiner.combineTraits(prevDate, date));
			}
			// we don't need to do anything when the traits are equal cause then the new date is already equally represented by the existing one

			// recursive call to add the rest of the date that is after prevDate
			add(new TraitDate(prevDate.getEnd(), date.getEnd(), date.getTraits()));
		}

	}

	private void putDate(final Integer start, final Integer end, final List<String> traits) {
		dates.put(start, new TraitDate(start, end, traits));
	}

	public void remove(final Integer startOfDateToRemove) {
		final TraitDate origDate = dates.get(startOfDateToRemove);
		TraitDate newDate = new TraitDate(origDate.getStart(), origDate.getEnd());

		// check if date can be merged with the previous date
		final Map.Entry<Integer, TraitDate> prevEntry = dates.lowerEntry(startOfDateToRemove);
		if (prevEntry != null && traitCombiner.areTraitsEqual(prevEntry.getValue(), newDate)) {
			newDate = new TraitDate(prevEntry.getValue().getStart(), newDate.getEnd());
			dates.remove(prevEntry.getKey());
		}
		// check if date can be merged with the next date
		final Map.Entry<Integer, TraitDate> nextDate = dates.higherEntry(startOfDateToRemove);
		if (nextDate != null && traitCombiner.areTraitsEqual(nextDate.getValue(), newDate)) {
			newDate = new TraitDate(newDate.getStart(), nextDate.getValue().getEnd());
			dates.remove(nextDate.getKey());
		}
		/* we don't need to check any more dates, cause as you check to merge on any change there shouldn't be any two
			dates that can be merged additionally to one inserted/removed */

		dates.put(newDate.getStart(), newDate);
	}

	public void modifyList(final LinearDateListModifier listModifier) {
		Integer curKey = dates.firstKey();
		do {
			final TraitDate prevDate = dates.lowerEntry(curKey) != null ? dates.lowerEntry(curKey).getValue() : null;
			final TraitDate nextDate = dates.higherEntry(curKey) != null ? dates.higherEntry(curKey).getValue() : null;
			final List<TraitDate> replacementDates = listModifier.modifyDate(prevDate, dates.get(curKey), nextDate);

			remove(curKey);
			for (TraitDate date : replacementDates) {
				add(date);
			}
		} while (!curKey.equals(dates.lastKey()));
	}

	public Collection<TraitDate> getDates() {
		return dates.values();
	}
}
