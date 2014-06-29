package de.unipotsdam.cs.groupplaner.user.resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.BlockedDate;
import de.unipotsdam.cs.groupplaner.user.dao.BlockedDatesDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Component
@Path(PathConfig.BLOCKED_DATES_RESOURCE_PATH)
@Produces({MediaType.APPLICATION_JSON})
public class BlockedDatesResource {

	@Autowired
	private BlockedDatesDAO blockedDatesDAO;
	@Autowired
	private SecurityContextFacade securityContextFacade;

	@GET
	public Response getAllBlockedDates(@QueryParam("source") final String sourceFilter) {
		final ImmutableList<BlockedDate> blockedDates;
		if (sourceFilter == null) {
			blockedDates = blockedDatesDAO.getBlockedDates(securityContextFacade.getCurrentUserEmail());
		} else {
			blockedDates = blockedDatesDAO.getBlockedDates(securityContextFacade.getCurrentUserEmail(), sourceFilter);
		}

		return Response.status(Response.Status.OK).entity(blockedDates).build();
	}

	// TODO: handle overlapping dates

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	public Response createBlockedDate(@RequestBody final Map<String, Object> data) {
		Preconditions.checkNotNull(data.get("start"));
		Preconditions.checkNotNull(data.get("end"));
		Preconditions.checkNotNull(data.get("source"));

		BlockedDate newBlockedDate = new BlockedDate((Integer) data.get("start"), (Integer) data.get("end"), securityContextFacade.getCurrentUserEmail(), (String) data.get("source"));

		final BlockedDate createdBlockedDate = blockedDatesDAO.getBlockedDate(blockedDatesDAO.createBlockedDate(newBlockedDate));

		return Response.status(Response.Status.CREATED).entity(createdBlockedDate).build();
	}

	@GET
	@Path("/{id}")
	public Response getBlockedDate(@PathParam("id") final Integer id) {
		final BlockedDate blockedDate = checkAndGetBlockedDate(id);

		return Response.status(Response.Status.OK).entity(blockedDate).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response updateBlockedDate(@PathParam("id") final Integer id, @RequestBody final Map<String, Object> data) {
		Preconditions.checkNotNull(data.get("start"));
		Preconditions.checkNotNull(data.get("end"));
		Preconditions.checkNotNull(data.get("source"));

		checkAndGetBlockedDate(id);

		BlockedDate modifiedBlockedDate = new BlockedDate(id, (Integer) data.get("start"), (Integer) data.get("end"), securityContextFacade.getCurrentUserEmail(), (String) data.get("source"));
		final Boolean updateSuccessful = blockedDatesDAO.updateBlockedDate(modifiedBlockedDate);
		if (!updateSuccessful) {
			throw new EmptyResultDataAccessException(1);
		}
		modifiedBlockedDate = blockedDatesDAO.getBlockedDate(modifiedBlockedDate.getId());

		return Response.status(Response.Status.OK).entity(modifiedBlockedDate).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteBlockedDate(@PathParam("id") final Integer id) {
		checkAndGetBlockedDate(id);

		blockedDatesDAO.deleteBlockedDate(id);

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	private BlockedDate checkAndGetBlockedDate(final Integer id) {
		final BlockedDate blockedDate = blockedDatesDAO.getBlockedDate(id);

		if (!blockedDate.getUserEmail().equals(securityContextFacade.getCurrentUserEmail())) {
			throw new AccessDeniedException("This date does not belong to the specified user.");
		}

		return blockedDate;
	}
}
