package de.unipotsdam.cs.groupplaner.datefinder.list.traitdateimpl;

import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStreamDateCreator;
import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TraitDateDateCreator implements ConsecutiveDateStreamDateCreator<TraitDate> {
	@Override
	public TraitDate createDate(Integer start, Integer end, TraitDate origDate) {
		final Map<String, Object> traits = origDate == null ? null : origDate.getTraits();
		return new TraitDate(start, end, traits);
	}
}
