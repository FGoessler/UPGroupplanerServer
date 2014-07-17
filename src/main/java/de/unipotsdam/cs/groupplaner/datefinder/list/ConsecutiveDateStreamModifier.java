package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.PeriodDate;

import java.util.List;

public interface ConsecutiveDateStreamModifier<D extends PeriodDate> {
	/**
	 * The curDate passed to this method will be removed from the LinearDateList and the elements of the
	 * returned list will be inserted instead of it, so that the list stays sorted.
	 * The next call of this method will deliver the successor of the original curDate.
	 * Return a list with only curDate in it if you don't want to modify the LinearDateList.
	 *
	 * @param prevDate The previous date. If there is no real previous date it'll be the last date of the week.
	 * @param curDate  The current date.
	 * @param nextDate The next date. If the last date, which you returned from the last call, was merged with its
	 *                 successor it will be this merged date. If there is no real next date, cause curDate is the
	 *                 last date of the week it'll be the first date of the week.
	 * @return The dates, which should replace curDate.
	 */
	public List<D> modifyDate(final D prevDate, final D curDate, final D nextDate);
}
