package de.unipotsdam.cs.groupplaner.datefinder.list.prioritizeddateimpl;


import de.unipotsdam.cs.groupplaner.datefinder.list.DateCombiner;
import de.unipotsdam.cs.groupplaner.domain.dates.PrioritizedDate;
import de.unipotsdam.cs.groupplaner.domain.dates.TraitDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrioritizedDateCombiner implements DateCombiner<PrioritizedDate> {

	@Autowired
	private DateCombiner<TraitDate> traitDateDateCombiner;

	@Override
	public Boolean areAdditionalDatePropertiesEqual(PrioritizedDate date1, PrioritizedDate date2) {
		final Boolean prioritiesAreEqual = date1.getPriority().equals(date2.getPriority());

		return prioritiesAreEqual && traitDateDateCombiner.areAdditionalDatePropertiesEqual(date1, date2);
	}

	@Override
	public PrioritizedDate createDateWithCombinedProperties(Integer start, Integer end, PrioritizedDate oldDate, PrioritizedDate newDate) {
		final TraitDate combinedTraitDate = traitDateDateCombiner.createDateWithCombinedProperties(start, end, oldDate, newDate);
		final int newPriority = newDate.getPriority();
		return new PrioritizedDate(start, end, newPriority, combinedTraitDate.getTraits());
	}
}
