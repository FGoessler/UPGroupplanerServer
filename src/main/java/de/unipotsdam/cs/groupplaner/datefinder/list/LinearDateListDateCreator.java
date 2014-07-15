package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.TraitDate;

public interface LinearDateListDateCreator<D extends TraitDate> {
	public D createDate(final Integer start, final Integer end, final D origDate);
}
