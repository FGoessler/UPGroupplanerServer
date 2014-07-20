package de.unipotsdam.cs.groupplaner.group.resource;

import com.google.common.base.Preconditions;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.AcceptedDate;
import de.unipotsdam.cs.groupplaner.group.service.AcceptedDatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(PathConfig.ACCEPTED_DATES_RESOURCE_PATH)
public class AcceptedDatesResource {

	@Autowired
	private AcceptedDatesService acceptedDatesService;

	@RequestMapping(method = RequestMethod.GET)
	public List<AcceptedDate> getAcceptedDates(@PathVariable("id") final Integer groupId) {
		return acceptedDatesService.getAcceptedDates(groupId);
	}

	// TODO: handle overlapping dates

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public AcceptedDate createAcceptedDate(@PathVariable("id") final Integer groupId, @RequestBody final Map<String, Object> data) {
		Preconditions.checkNotNull(data.get("start"));
		Preconditions.checkNotNull(data.get("end"));

		return acceptedDatesService.createAcceptedDate(groupId, (Integer) data.get("start"), (Integer) data.get("end"));
	}

	@RequestMapping(value = "/{dateId}", method = RequestMethod.GET)
	public AcceptedDate getAcceptedDate(@PathVariable("id") final Integer groupId, @PathVariable("dateId") final Integer id) {
		return acceptedDatesService.getAcceptedDate(groupId, id);
	}

	@RequestMapping(value = "/{dateId}", method = RequestMethod.PUT)
	public AcceptedDate updateAcceptedDate(@PathVariable("id") final Integer groupId, @PathVariable("dateId") final Integer id, @RequestBody final Map<String, Object> data) {
		Preconditions.checkNotNull(data.get("start"));
		Preconditions.checkNotNull(data.get("end"));

		return acceptedDatesService.updateAcceptedDate(groupId, id, (Integer) data.get("start"), (Integer) data.get("end"));
	}

	@RequestMapping(value = "/{dateId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAcceptedDate(@PathVariable("id") final Integer groupId, @PathVariable("dateId") final Integer id) {
		acceptedDatesService.deleteAcceptedDate(groupId, id);
	}
}
