package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.PeriodDate;

public interface ConsecutiveDateStreamDateCreator<D extends PeriodDate> {
	/**
	 * Return a new as neutral as possible date. You might use properties from the origDate, which is the date the
	 * created new date will partially or fully replace cause of a add or remove operation.
	 *
	 * @param start    The start time of the date to create.
	 * @param end      The end time of the date to create.
	 * @param origDate The date which the created new date will partially or fully replace cause of a add or remove operation.
	 * @return A new date.
	 */
	public D createDate(final Integer start, final Integer end, final D origDate);
}
