package de.unipotsdam.cs.groupplaner.datefinder.list.prioritizeddateimpl;


import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.datefinder.list.DateCombiner;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class PrioritizedDateCombiner implements DateCombiner<PrioritizedDate> {

	@Override
	public Boolean areAdditionalDatePropertiesEqual(PrioritizedDate date1, PrioritizedDate date2) {
		final Boolean prioritiesAreEqual = date1.getPriority().equals(date2.getPriority());
		final Boolean boothAreBlocked = date1.hasTrait(TraitDate.TRAIT_BLOCKED_DATE) && date2.hasTrait(TraitDate.TRAIT_BLOCKED_DATE);
		final Boolean bothAreAccepted = date1.hasTrait(TraitDate.TRAIT_ACCEPTED_DATE) && date2.hasTrait(TraitDate.TRAIT_ACCEPTED_DATE);
		final Boolean bothAreWithoutTraits = date1.getTraits().size() == 0 && date2.getTraits().size() == 0;

		return prioritiesAreEqual && (boothAreBlocked || bothAreAccepted || bothAreWithoutTraits);
	}

	@Override
	public PrioritizedDate createDateWithCombinedProperties(Integer start, Integer end, PrioritizedDate oldDate, PrioritizedDate newDate) {
		Set<String> traits = new HashSet<String>(oldDate.getTraits());
		traits.addAll(newDate.getTraits());
		final int newPriority = newDate.getPriority();
		return new PrioritizedDate(start, end, newPriority, ImmutableList.copyOf(traits));
	}
}
