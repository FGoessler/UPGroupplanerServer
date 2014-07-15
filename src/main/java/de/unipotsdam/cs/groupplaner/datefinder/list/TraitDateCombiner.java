package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.TraitDate;

public interface TraitDateCombiner<D extends TraitDate> {
	public Boolean areDateTraitsEqual(final D traitDate1, final D traitDate2);

	public D combineDates(final Integer start, final Integer end, final D oldDate, final D newDate);
}
