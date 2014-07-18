package de.unipotsdam.cs.groupplaner.datefinder.list.prioritizeddateimpl;


import de.unipotsdam.cs.groupplaner.datefinder.list.ConsecutiveDateStreamDateCreator;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PrioritizedDateStreamDateCreator implements ConsecutiveDateStreamDateCreator<PrioritizedDate> {
	@Override
	public PrioritizedDate createDate(Integer start, Integer end, PrioritizedDate origDate) {
		final Integer prio = origDate == null ? PrioritizedDate.PRIORITY_NEUTRAL : origDate.getPriority();
		final Map<String, Object> traits = origDate == null ? null : origDate.getTraits();
		return new PrioritizedDate(start, end, prio, traits);
	}
}
