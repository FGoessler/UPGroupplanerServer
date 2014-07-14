package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.TraitDate;

import java.util.List;

public interface LinearDateListModifier {
	/**
	 * The curDate passed to this method will be removed from the LinearDateList and the elements of the
	 * returned list will be inserted instead of it, so that the list stays sorted.
	 * The next call of this method will deliver the successor of the original curDate, which now might be one you just
	 * returned in the list of the previous call.
	 * Return a list with only curDate in it if you don't want to modify the LinearDateList.
	 *
	 * @param prevDate The previous date. Might be null if curDate is the first date in the list.
	 * @param curDate  The current date.
	 * @param nextDate The next date.  Might be null if curDate is the last date in the list.
	 * @return The dates, which should replace curDate.
	 */
	public List<TraitDate> modifyDate(final TraitDate prevDate, final TraitDate curDate, final TraitDate nextDate);
}
