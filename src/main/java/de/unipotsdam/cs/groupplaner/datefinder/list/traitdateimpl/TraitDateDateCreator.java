package de.unipotsdam.cs.groupplaner.datefinder.list.traitdateimpl;

import de.unipotsdam.cs.groupplaner.datefinder.list.LinearDateListDateCreator;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TraitDateDateCreator implements LinearDateListDateCreator<TraitDate> {
	@Override
	public TraitDate createDate(Integer start, Integer end, TraitDate origDate) {
		final List<String> traits = origDate == null ? null : origDate.getTraits();
		return new TraitDate(start, end, traits);
	}
}
