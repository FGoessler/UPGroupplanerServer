package de.unipotsdam.cs.groupplaner.resource;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.AcceptedDate;
import de.unipotsdam.cs.groupplaner.repository.AcceptedDatesRepository;
import de.unipotsdam.cs.groupplaner.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Component
@Path(PathConfig.ACCEPTED_DATES_RESOURCE_PATH)
@Produces({MediaType.APPLICATION_JSON})
public class AcceptedDatesResource {

	@Autowired
	private AcceptedDatesRepository acceptedDatesRepository;
	@Autowired
	private GroupService groupService;

	@GET
	public Response getAcceptedDates(@PathParam("id") final Integer groupId) throws Exception {
		final ImmutableList<AcceptedDate> acceptedDates = acceptedDatesRepository.getAcceptedDates(groupId);
		return Response.status(Response.Status.OK).entity(acceptedDates).build();
	}

	// TODO: handle overlapping dates

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	public Response createAcceptedDate(@PathParam("id") final Integer groupId, @RequestBody final Map<String, Object> data) throws Exception {
		AcceptedDate newAcceptedDate = new AcceptedDate((Integer) data.get("start"), (Integer) data.get("end"), groupId);

		final Integer createdDateId = acceptedDatesRepository.createAcceptedDate(newAcceptedDate);
		final AcceptedDate createdDate = acceptedDatesRepository.getAcceptedDate(createdDateId);

		return Response.status(Response.Status.CREATED).entity(createdDate).build();
	}

	@GET
	@Path("/{dateId}")
	public Response getAcceptedDate(@PathParam("id") final Integer groupId, @PathParam("dateId") final Integer id) throws Exception {
		groupService.validateUsersPermissionForGroup(groupId);
		validateGroupMatchesDate(groupId, id);

		final AcceptedDate date = acceptedDatesRepository.getAcceptedDate(id);
		return Response.status(Response.Status.OK).entity(date).build();
	}

	@PUT
	@Path("/{dateId}")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response updateAcceptedDate(@PathParam("id") final Integer groupId, @PathParam("dateId") final Integer id, @RequestBody final Map<String, Object> data) throws Exception {
		groupService.validateUsersPermissionForGroup(groupId);
		validateGroupMatchesDate(groupId, id);

		AcceptedDate modifiedDate = new AcceptedDate(id, (Integer) data.get("start"), (Integer) data.get("end"), groupId);
		final Boolean updateSuccessful = acceptedDatesRepository.updateAcceptedDate(modifiedDate);
		if (!updateSuccessful) {
			throw new EmptyResultDataAccessException(1);
		}
		modifiedDate = acceptedDatesRepository.getAcceptedDate(modifiedDate.getId());

		return Response.status(Response.Status.OK).entity(modifiedDate).build();
	}

	@DELETE
	@Path("/{dateId}")
	public Response deleteAcceptedDate(@PathParam("id") final Integer groupId, @PathParam("dateId") final Integer id) throws Exception {
		groupService.validateUsersPermissionForGroup(groupId);
		validateGroupMatchesDate(groupId, id);

		final Boolean deletionSuccessful = acceptedDatesRepository.deleteAcceptedDate(id);
		if (!deletionSuccessful) {
			throw new EmptyResultDataAccessException(1);
		}

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	private void validateGroupMatchesDate(final Integer groupId, final Integer dateId) {
		final AcceptedDate date = acceptedDatesRepository.getAcceptedDate(dateId);
		if (!date.getGroup().equals(groupId)) {
			throw new IllegalArgumentException("The date does not belong to specified group.");
		}
	}
}
