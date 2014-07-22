package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.dates.PeriodDate;

public interface DateCombiner<D extends PeriodDate> {
	/**
	 * Compare the two given dates on equality but ignore start and end point.
	 */
	public Boolean areAdditionalDatePropertiesEqual(final D date1, final D date2);

	/**
	 * Create a new date by using the given start and end time and derive any additional properties of the custom
	 * PeriodDate subclass by combining them from oldDate and newDate.
	 */
	public D createDateWithCombinedProperties(final Integer start, final Integer end, final D oldDate, final D newDate);
}
