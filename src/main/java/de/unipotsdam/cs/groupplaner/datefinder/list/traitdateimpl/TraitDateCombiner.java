package de.unipotsdam.cs.groupplaner.datefinder.list.traitdateimpl;


import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.unipotsdam.cs.groupplaner.datefinder.list.DateCombiner;
import de.unipotsdam.cs.groupplaner.domain.dates.TraitDate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

@Component
public class TraitDateCombiner implements DateCombiner<TraitDate> {

	@Override
	public Boolean areAdditionalDatePropertiesEqual(TraitDate date1, TraitDate date2) {
		final Boolean bothAreNotBlocked = !date1.hasTrait(TraitDate.TRAIT_BLOCKED_DATE) && !date2.hasTrait(TraitDate.TRAIT_BLOCKED_DATE);
		final Boolean sameAcceptedTrait = date1.hasTrait(TraitDate.TRAIT_ACCEPTED_DATE) && date2.hasTrait(TraitDate.TRAIT_ACCEPTED_DATE) &&
				date1.getTrait(TraitDate.TRAIT_ACCEPTED_DATE).equals(date2.getTrait(TraitDate.TRAIT_ACCEPTED_DATE));

		return bothAreNotBlocked && sameAcceptedTrait;
	}

	@Override
	public TraitDate createDateWithCombinedProperties(Integer start, Integer end, TraitDate oldDate, TraitDate newDate) {
		final HashMap<String, Object> combinedTraits = Maps.newHashMap(oldDate.getTraits());
		combinedTraits.putAll(newDate.getTraits());
		if (newDate.hasTrait(TraitDate.TRAIT_BLOCKED_DATE) && oldDate.hasTrait(TraitDate.TRAIT_BLOCKED_DATE)) {
			final Set<String> oldDateBlockedMembers = (Set<String>) oldDate.getTrait(TraitDate.TRAIT_BLOCKED_DATE);
			final Set<String> newDateBlockedMembers = (Set<String>) newDate.getTrait(TraitDate.TRAIT_BLOCKED_DATE);
			final Sets.SetView<String> unionBlockedMembers = Sets.union(oldDateBlockedMembers, newDateBlockedMembers);
			combinedTraits.put(TraitDate.TRAIT_BLOCKED_DATE, unionBlockedMembers);
		}
		return new TraitDate(start, end, combinedTraits);
	}
}
