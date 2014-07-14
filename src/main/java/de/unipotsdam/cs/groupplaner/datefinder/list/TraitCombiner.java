package de.unipotsdam.cs.groupplaner.datefinder.list;


import de.unipotsdam.cs.groupplaner.domain.TraitDate;

import java.util.List;

public interface TraitCombiner {
	public Boolean areTraitsEqual(final TraitDate traitDate1, final TraitDate traitDate2);

	public List<String> combineTraits(final TraitDate traitDate1, final TraitDate traitDate2);
}
