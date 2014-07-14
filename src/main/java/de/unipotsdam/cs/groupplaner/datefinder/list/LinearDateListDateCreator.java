package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.TraitDate;

import java.util.List;

public interface LinearDateListDateCreator<D extends TraitDate> {
	public D createDate(final Integer start, final Integer end, final List<String> traits);
}
