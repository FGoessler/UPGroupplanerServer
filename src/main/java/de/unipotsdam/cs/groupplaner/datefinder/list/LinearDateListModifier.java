package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.TraitDate;

import java.util.List;

public interface LinearDateListModifier {
	/**
	 * The curDate and NextDate passed to this method will be removed from the LinearDateList and the elements of the
	 * returned list will be inserted after the original prevDate. Afterwards the LinearDateList will be sorted.
	 * The next call of this method will deliver the successor of the first element in the returned list, which in turn
	 * doesn't need to be the nextDate of the previous call.
	 * Return a list with only curDate and nextDate if you don't want to modify the LinearDateList.
	 *
	 * @param prevDate The previous date. Might be null if curDate is the first date in the list.
	 * @param curDate  The current date.
	 * @param nextDate The next date.  Might be null if curDate is the last date in the list.
	 * @return The dates, which should replace curDate and nextDate.
	 */
	public List<TraitDate> modifyDate(final TraitDate prevDate, final TraitDate curDate, final TraitDate nextDate);
}
