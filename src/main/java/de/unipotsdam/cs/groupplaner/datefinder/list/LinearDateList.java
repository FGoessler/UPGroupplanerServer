package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.PeriodDate;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LinearDateList<D extends TraitDate> {

	final private TreeMap<Integer, D> dates;

	final private TraitDateCombiner<D> traitDateCombiner;
	final private LinearDateListDateCreator<D> dateCreator;

	public LinearDateList(final TraitDateCombiner<D> traitDateCombiner, final LinearDateListDateCreator<D> dateCreator) {
		this.traitDateCombiner = traitDateCombiner;
		this.dateCreator = dateCreator;

		dates = new TreeMap<Integer, D>();
		dates.put(PeriodDate.START_OF_WEEK, dateCreator.createDate(PeriodDate.START_OF_WEEK, PeriodDate.END_OF_WEEK, null));
	}

	public void add(final D date) {
		// split dates that overflow at the end of the week
		if (date.getEnd() < date.getStart()) {
			add(dateCreator.createDate(PeriodDate.START_OF_WEEK, date.getEnd(), date));
			add(dateCreator.createDate(date.getStart(), PeriodDate.END_OF_WEEK, date));
			return;
		}

		final Map.Entry<Integer, D> prevDateEntry = dates.floorEntry(date.getStart());
		final D prevDate = prevDateEntry.getValue();
		final Integer prevKey = prevDateEntry.getKey();

		if (date.getEnd() <= prevDate.getEnd()) {    // new date is fully contained in prevDate
			if (!traitDateCombiner.areDateTraitsEqual(prevDate, date)) {
				// only create a date before the new date if doesn't start at the same time as the previous date
				if (prevDate.getStart() < date.getStart()) putDate(prevKey, date.getStart(), prevDate);

				// create the date for the overlapping period with combined traits
				putDate(traitDateCombiner.combineDates(date.getStart(), date.getEnd(), prevDate, date));

				// only create a date after the new date if doesn't end at the same time as the previous date
				if (date.getEnd() < prevDate.getEnd()) putDate(date.getEnd(), prevDate.getEnd(), prevDate);
			}
			// we don't need to do anything when the traits are equal cause then the new date is already equally represented by the existing one
		} else {                                    // new date overlaps to later dates
			if (!traitDateCombiner.areDateTraitsEqual(prevDate, date)) {
				// only create a date before the new date if doesn't start at the same time as the previous date
				if (prevDate.getStart() < date.getStart()) putDate(prevKey, date.getStart(), prevDate);

				// create the date for the overlapping period with combined traits
				putDate(traitDateCombiner.combineDates(date.getStart(), prevDate.getEnd(), prevDate, date));
			}
			// we don't need to do anything when the traits are equal cause then the new date is already equally represented by the existing one

			// recursive call to add the rest of the date that is after prevDate
			add(dateCreator.createDate(prevDate.getEnd(), date.getEnd(), date));
		}

	}

	private void putDate(final Integer start, final Integer end, final D origDate) {
		dates.put(start, dateCreator.createDate(start, end, origDate));
	}

	private void putDate(final D date) {
		dates.put(date.getStart(), date);
	}

	public void remove(final Integer startOfDateToRemove) {
		final D origDate = dates.get(startOfDateToRemove);
		D newDate = dateCreator.createDate(origDate.getStart(), origDate.getEnd(), null);

		// check if date can be merged with the previous date
		final Map.Entry<Integer, D> prevEntry = dates.lowerEntry(startOfDateToRemove);
		if (prevEntry != null && traitDateCombiner.areDateTraitsEqual(prevEntry.getValue(), newDate)) {
			newDate = dateCreator.createDate(prevEntry.getValue().getStart(), newDate.getEnd(), null);
			dates.remove(prevEntry.getKey());
		}
		// check if date can be merged with the next date
		final Map.Entry<Integer, D> nextDate = dates.higherEntry(startOfDateToRemove);
		if (nextDate != null && traitDateCombiner.areDateTraitsEqual(nextDate.getValue(), newDate)) {
			newDate = dateCreator.createDate(newDate.getStart(), nextDate.getValue().getEnd(), null);
			dates.remove(nextDate.getKey());
		}
		/* we don't need to check any more dates, cause as you check to merge on any change there shouldn't be any two
			dates that can be merged additionally to one inserted/removed */

		dates.put(newDate.getStart(), newDate);
	}

	public void modifyList(final LinearDateListModifier<D> listModifier) {
		Integer curKey = dates.firstKey();
		do {
			final Map.Entry<Integer, D> prevEntry = dates.lowerEntry(curKey) == null ? dates.lastEntry() : dates.lowerEntry(curKey);
			final Map.Entry<Integer, D> nextEntry = dates.higherEntry(curKey) == null ? dates.firstEntry() : dates.higherEntry(curKey);
			final D curDate = dates.get(curKey);
			final List<D> replacementDates = listModifier.modifyDate(prevEntry.getValue(), curDate, nextEntry.getValue());

			remove(curKey);
			for (D date : replacementDates) {
				add(date);
			}

			curKey = curDate.getEnd();
		} while (curKey <= dates.lastKey());
	}

	public Collection<D> getDates() {
		return dates.values();
	}
}
