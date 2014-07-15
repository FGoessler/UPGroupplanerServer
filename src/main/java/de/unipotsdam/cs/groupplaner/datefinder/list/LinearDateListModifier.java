package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.TraitDate;

import java.util.List;

public interface LinearDateListModifier<D extends TraitDate> {
	/**
	 * The curDate passed to this method will be removed from the LinearDateList and the elements of the
	 * returned list will be inserted instead of it, so that the list stays sorted.
	 * The next call of this method will deliver the successor of the original curDate.
	 * Return a list with only curDate in it if you don't want to modify the LinearDateList.
	 *
	 * @param prevDate The previous date. If there is no real previous date it'll be the last date of the list.
	 * @param curDate  The current date.
	 * @param nextDate The next date.  If there is no real next date it'll be the first date of the list.
	 * @return The dates, which should replace curDate.
	 */
	public List<D> modifyDate(final D prevDate, final D curDate, final D nextDate);
}
