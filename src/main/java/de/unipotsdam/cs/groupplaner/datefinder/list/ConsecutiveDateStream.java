package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.PeriodDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class manages a list of PeriodDates in a consecutive stream. That means for any given point in time in a week
 * there can only be one PeriodDate.
 * This property is ensured with each add and remove operation.
 * You need to pass a ConsecutiveDateStreamDateCreator which is responsible for generating new dates for a given
 * period of time.
 * You also need to pass a DateCombiner, which is used to check whether to dates are equal beside their start and end
 * time and to combine he properties of to dates to create a new date.
 * Use the modifyList method and a custom ConsecutiveDateStreamModifier to change properties on dates or split dates or
 * do any other "batch" modifications.
 *
 * @param <D> The concrete subclass of PeriodDate which should be managed.
 */
public class ConsecutiveDateStream<D extends PeriodDate> {

	final private TreeMap<Integer, D> dates;

	final private DateCombiner<D> dateCombiner;
	final private ConsecutiveDateStreamDateCreator<D> dateCreator;

	public ConsecutiveDateStream(final DateCombiner<D> dateCombiner, final ConsecutiveDateStreamDateCreator<D> dateCreator) {
		this.dateCombiner = dateCombiner;
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
			if (!dateCombiner.areAdditionalDatePropertiesEqual(prevDate, date)) {
				// only create a date before the new date if doesn't start at the same time as the previous date
				if (prevDate.getStart() < date.getStart()) putDate(prevKey, date.getStart(), prevDate);

				// create the date for the overlapping period with combined traits
				putDate(dateCombiner.createDateWithCombinedProperties(date.getStart(), date.getEnd(), prevDate, date));

				// only create a date after the new date if doesn't end at the same time as the previous date
				if (date.getEnd() < prevDate.getEnd()) putDate(date.getEnd(), prevDate.getEnd(), prevDate);
			}
			// we don't need to do anything when the traits are equal cause then the new date is already equally represented by the existing one
		} else {                                    // new date overlaps to later dates
			if (!dateCombiner.areAdditionalDatePropertiesEqual(prevDate, date)) {
				// only create a date before the new date if doesn't start at the same time as the previous date
				if (prevDate.getStart() < date.getStart()) putDate(prevKey, date.getStart(), prevDate);

				// create the date for the overlapping period with combined traits
				putDate(dateCombiner.createDateWithCombinedProperties(date.getStart(), prevDate.getEnd(), prevDate, date));
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
		dates.remove(startOfDateToRemove);
		D newDate = dateCreator.createDate(origDate.getStart(), origDate.getEnd(), null);

		// check if date can be merged with the previous date
		final Map.Entry<Integer, D> prevEntry = dates.lowerEntry(startOfDateToRemove);
		if (prevEntry != null && dateCombiner.areAdditionalDatePropertiesEqual(prevEntry.getValue(), newDate)) {
			newDate = dateCreator.createDate(prevEntry.getValue().getStart(), newDate.getEnd(), null);
			dates.remove(prevEntry.getKey());
		}
		// check if date can be merged with the next date
		final Map.Entry<Integer, D> nextDate = dates.higherEntry(startOfDateToRemove);
		if (nextDate != null && dateCombiner.areAdditionalDatePropertiesEqual(nextDate.getValue(), newDate)) {
			newDate = dateCreator.createDate(newDate.getStart(), nextDate.getValue().getEnd(), null);
			dates.remove(nextDate.getKey());
		}
		/* we don't need to check any more dates, cause as you check to merge on any change there shouldn't be any two
			dates that can be merged additionally to one inserted/removed */

		dates.put(newDate.getStart(), newDate);
	}

	public void modifyList(final ConsecutiveDateStreamModifier<D> listModifier) {
		Integer curKey = dates.firstKey();
		do {
			final Map.Entry<Integer, D> curEntry = dates.floorEntry(curKey);
			final List<D> replacementDates = listModifier.modifyDate(this, curEntry.getValue());

			remove(curEntry.getKey());
			for (D date : replacementDates) {
				add(date);
			}

			curKey = curEntry.getValue().getEnd();
		} while (curKey <= dates.lastKey());
	}

	public Collection<D> getDates() {
		return dates.values();
	}

	/**
	 * Returns the previous date to the given date. If there is no real previous date, cause curDate is the first date
	 * of the week, it'll be the last date of the week.
	 */
	public D predeccessorDate(final D curDate) {
		Integer curKey = curDate.getStart();
		return dates.lowerEntry(curKey) == null ? dates.lastEntry().getValue() : dates.lowerEntry(curKey).getValue();
	}

	/**
	 * Returns the next date. If there is no real next date, cause curDate is the last date of the week, it'll be the
	 * first date of the week.
	 */
	public D successorDate(final D curDate) {
		Integer curKey = curDate.getStart();
		return dates.higherEntry(curKey) == null ? dates.firstEntry().getValue() : dates.higherEntry(curKey).getValue();
	}
}
