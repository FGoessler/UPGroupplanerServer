package de.unipotsdam.cs.groupplaner.datefinder.list;

import de.unipotsdam.cs.groupplaner.domain.TraitDate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TraitDateDateCreator implements LinearDateListDateCreator<TraitDate> {
	@Override
	public TraitDate createDate(Integer start, Integer end, List<String> traits) {
		return new TraitDate(start, end, traits);
	}
}
