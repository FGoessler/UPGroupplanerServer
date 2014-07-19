package de.unipotsdam.cs.groupplaner.datefinder.list.traitdateimpl;


import com.google.common.collect.Maps;
import de.unipotsdam.cs.groupplaner.datefinder.list.DateCombiner;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class TraitDateCombiner implements DateCombiner<TraitDate> {

	@Override
	public Boolean areAdditionalDatePropertiesEqual(TraitDate date1, TraitDate date2) {
		final Boolean sameBlockedTrait = date1.hasTrait(TraitDate.TRAIT_BLOCKED_DATE) && date2.hasTrait(TraitDate.TRAIT_BLOCKED_DATE) &&
				date1.getTrait(TraitDate.TRAIT_BLOCKED_DATE).equals(date2.getTrait(TraitDate.TRAIT_BLOCKED_DATE));
		final Boolean sameAcceptedTrait = date1.hasTrait(TraitDate.TRAIT_ACCEPTED_DATE) && date2.hasTrait(TraitDate.TRAIT_ACCEPTED_DATE) &&
				date1.getTrait(TraitDate.TRAIT_ACCEPTED_DATE).equals(date2.getTrait(TraitDate.TRAIT_ACCEPTED_DATE));

		return sameBlockedTrait && sameAcceptedTrait;
	}

	@Override
	public TraitDate createDateWithCombinedProperties(Integer start, Integer end, TraitDate oldDate, TraitDate newDate) {
		final HashMap<String, Object> combinedTraits = Maps.newHashMap(oldDate.getTraits());
		combinedTraits.putAll(newDate.getTraits());
		if (newDate.hasTrait(TraitDate.TRAIT_BLOCKED_DATE) && oldDate.hasTrait(TraitDate.TRAIT_BLOCKED_DATE)) {
			final Integer blockedDateTraitSum = ((Integer) newDate.getTrait(TraitDate.TRAIT_BLOCKED_DATE)) + ((Integer) oldDate.getTrait(TraitDate.TRAIT_BLOCKED_DATE));
			combinedTraits.put(TraitDate.TRAIT_BLOCKED_DATE, blockedDateTraitSum);
		}
		return new TraitDate(start, end, combinedTraits);
	}
}
