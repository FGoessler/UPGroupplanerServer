package de.unipotsdam.cs.groupplaner.resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.AcceptedDate;
import de.unipotsdam.cs.groupplaner.service.AcceptedDatesService;
import org.springframework.beans.factory.annotation.Autowired;
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
	private AcceptedDatesService acceptedDatesService;

	@GET
	public Response getAcceptedDates(@PathParam("id") final Integer groupId) {
		final ImmutableList<AcceptedDate> acceptedDates = acceptedDatesService.getAcceptedDates(groupId);
		return Response.status(Response.Status.OK).entity(acceptedDates).build();
	}

	// TODO: handle overlapping dates

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	public Response createAcceptedDate(@PathParam("id") final Integer groupId, @RequestBody final Map<String, Object> data) {
		Preconditions.checkNotNull(data.get("start"));
		Preconditions.checkNotNull(data.get("end"));

		AcceptedDate createdDate = acceptedDatesService.createAcceptedDate(groupId, (Integer) data.get("start"), (Integer) data.get("end"));

		return Response.status(Response.Status.CREATED).entity(createdDate).build();
	}

	@GET
	@Path("/{dateId}")
	public Response getAcceptedDate(@PathParam("id") final Integer groupId, @PathParam("dateId") final Integer id) {
		return Response.status(Response.Status.OK).entity(acceptedDatesService.getAcceptedDate(groupId, id)).build();
	}

	@PUT
	@Path("/{dateId}")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response updateAcceptedDate(@PathParam("id") final Integer groupId, @PathParam("dateId") final Integer id, @RequestBody final Map<String, Object> data) {
		Preconditions.checkNotNull(data.get("start"));
		Preconditions.checkNotNull(data.get("end"));

		AcceptedDate modifiedDate = acceptedDatesService.updateAcceptedDate(groupId, id, (Integer) data.get("start"), (Integer) data.get("end"));

		return Response.status(Response.Status.OK).entity(modifiedDate).build();
	}

	@DELETE
	@Path("/{dateId}")
	public Response deleteAcceptedDate(@PathParam("id") final Integer groupId, @PathParam("dateId") final Integer id) {
		acceptedDatesService.deleteAcceptedDate(groupId, id);
		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
