package de.unipotsdam.cs.groupplaner.datefinder.list.traitdateimpl;


import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.datefinder.list.TraitDateCombiner;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DefaultTraitDateCombiner implements TraitDateCombiner<TraitDate> {

	@Override
	public Boolean areDateTraitsEqual(TraitDate date1, TraitDate date2) {
		final Boolean boothAreBlocked = date1.hasTrait(TraitDate.TRAIT_BLOCKED_DATE) && date2.hasTrait(TraitDate.TRAIT_BLOCKED_DATE);
		final Boolean bothAreAccepted = date1.hasTrait(TraitDate.TRAIT_ACCEPTED_DATE) && date2.hasTrait(TraitDate.TRAIT_ACCEPTED_DATE);
		final Boolean bothAreWithoutTraits = date1.getTraits().size() == 0 && date2.getTraits().size() == 0;

		return (boothAreBlocked || bothAreAccepted || bothAreWithoutTraits);
	}

	@Override
	public TraitDate combineDates(Integer start, Integer end, TraitDate newDate, TraitDate oldDate) {
		Set<String> traits = new HashSet<String>(newDate.getTraits());
		traits.addAll(oldDate.getTraits());
		return new TraitDate(start, end, ImmutableList.copyOf(traits));
	}
}
