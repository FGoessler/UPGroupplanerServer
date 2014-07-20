package de.unipotsdam.cs.groupplaner.datefinder.resource;

import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.datefinder.service.PotentialDatesService;
import de.unipotsdam.cs.groupplaner.domain.PrioritizedDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PotentialDatesResource {

	@Autowired
	private PotentialDatesService potentialDatesService;

	@RequestMapping(PathConfig.POTENTIAL_DATES_RESOURCE_PATH)
	public List<PrioritizedDate> getPotentialDates(@PathVariable("id") final Integer groupId) {
		return potentialDatesService.calculatePotentialDates(groupId);
	}

}
