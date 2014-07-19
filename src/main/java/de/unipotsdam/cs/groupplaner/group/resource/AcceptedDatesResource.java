package de.unipotsdam.cs.groupplaner.group.resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.AcceptedDate;
import de.unipotsdam.cs.groupplaner.group.service.AcceptedDatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@RestController
@RequestMapping(PathConfig.ACCEPTED_DATES_RESOURCE_PATH)
public class AcceptedDatesResource {

	@Autowired
	private AcceptedDatesService acceptedDatesService;

	@RequestMapping(method = RequestMethod.GET)
	public Response getAcceptedDates(@PathVariable("id") final Integer groupId) {
		final ImmutableList<AcceptedDate> acceptedDates = acceptedDatesService.getAcceptedDates(groupId);
		return Response.status(Response.Status.OK).entity(acceptedDates).build();
	}

	// TODO: handle overlapping dates

	@RequestMapping(method = RequestMethod.POST)
	@Consumes({MediaType.APPLICATION_JSON})
	public Response createAcceptedDate(@PathVariable("id") final Integer groupId, @RequestBody final Map<String, Object> data) {
		Preconditions.checkNotNull(data.get("start"));
		Preconditions.checkNotNull(data.get("end"));

		AcceptedDate createdDate = acceptedDatesService.createAcceptedDate(groupId, (Integer) data.get("start"), (Integer) data.get("end"));

		return Response.status(Response.Status.CREATED).entity(createdDate).build();
	}

	@RequestMapping(value = "/{dateId}", method = RequestMethod.GET)
	public Response getAcceptedDate(@PathVariable("id") final Integer groupId, @PathVariable("dateId") final Integer id) {
		return Response.status(Response.Status.OK).entity(acceptedDatesService.getAcceptedDate(groupId, id)).build();
	}

	@RequestMapping(value = "/{dateId}", method = RequestMethod.PUT)
	@Consumes({MediaType.APPLICATION_JSON})
	public Response updateAcceptedDate(@PathVariable("id") final Integer groupId, @PathVariable("dateId") final Integer id, @RequestBody final Map<String, Object> data) {
		Preconditions.checkNotNull(data.get("start"));
		Preconditions.checkNotNull(data.get("end"));

		AcceptedDate modifiedDate = acceptedDatesService.updateAcceptedDate(groupId, id, (Integer) data.get("start"), (Integer) data.get("end"));

		return Response.status(Response.Status.OK).entity(modifiedDate).build();
	}

	@RequestMapping(value = "/{dateId}", method = RequestMethod.DELETE)
	public Response deleteAcceptedDate(@PathVariable("id") final Integer groupId, @PathVariable("dateId") final Integer id) {
		acceptedDatesService.deleteAcceptedDate(groupId, id);
		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
