package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.dates.PeriodDate;

import java.util.List;

public interface ConsecutiveDateStreamModifier<D extends PeriodDate> {
	/**
	 * The curDate passed to this method will be removed from the LinearDateList and the elements of the
	 * returned list will be inserted instead of it, so that the list stays sorted.
	 * The next call of this method will deliver the successor of the original curDate. If the last date in the list of
	 * dates, which you returned from the last call, was merged with its successor that merged date will be passed as
	 * curDate in the call after the merge.
	 * Return a list with only curDate in it if you don't want to modify the LinearDateList.
	 *
	 * @param dateStream The ConsecutiveDateStream which you are modifying. You can get the previous and next date
	 *                   directly from here. Do NOT modify this dateStream directly since this might break the iterator
	 *                   logic!
	 * @param curDate    The current date.
	 * @return The dates, which should replace curDate.
	 */
	public List<D> modifyDate(final ConsecutiveDateStream<D> dateStream, final D curDate);
}

