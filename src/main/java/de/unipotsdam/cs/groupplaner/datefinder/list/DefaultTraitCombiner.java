package de.unipotsdam.cs.groupplaner.datefinder.list;


import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DefaultTraitCombiner implements TraitCombiner {

	@Override
	public Boolean areTraitsEqual(TraitDate traitDate1, TraitDate traitDate2) {
		if (traitDate1.hasTrait(TraitDate.TRAIT_BLOCKED_DATE) && traitDate2.hasTrait(TraitDate.TRAIT_BLOCKED_DATE))
			return true;
		if (traitDate2.hasTrait(TraitDate.TRAIT_ACCEPTED_DATE) && traitDate2.hasTrait(TraitDate.TRAIT_ACCEPTED_DATE))
			return true;
		return false;
	}

	@Override
	public List<String> combineTraits(TraitDate traitDate1, TraitDate traitDate2) {
		Set<String> traits = new HashSet<String>(traitDate1.getTraits());
		traits.addAll(traitDate2.getTraits());
		return ImmutableList.copyOf(traits);
	}
}
