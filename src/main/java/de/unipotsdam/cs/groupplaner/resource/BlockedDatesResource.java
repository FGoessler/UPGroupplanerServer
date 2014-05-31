package de.unipotsdam.cs.groupplaner.resource;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.BlockedDate;
import de.unipotsdam.cs.groupplaner.repository.BlockedDatesRepository;
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
	private BlockedDatesRepository blockedDatesRepository;
	@Autowired
	private SecurityContextFacade securityContextFacade;

	@GET
	public Response getAllBlockedDates() throws Exception {
		final ImmutableList<BlockedDate> blockedDates = blockedDatesRepository.getBlockedDates(securityContextFacade.getCurrentUserEmail());

		return Response.status(Response.Status.OK).entity(blockedDates).build();
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	public Response createBlockedDate(@RequestBody final Map<String, Object> data) throws Exception {
		BlockedDate newBlockedDate = new BlockedDate((Integer)data.get("start"), (Integer)data.get("end"), securityContextFacade.getCurrentUserEmail());

		final BlockedDate createdBlockedDate = blockedDatesRepository.getBlockedDate(blockedDatesRepository.createBlockedDate(newBlockedDate));

		return Response.status(Response.Status.CREATED).entity(createdBlockedDate).build();
	}

	@GET
	@Path("/{id}")
	public Response getBlockedDate(@PathParam("id") final Integer id) throws Exception {
		final BlockedDate blockedDate = checkAndGetBlockedDate(id);

		return Response.status(Response.Status.OK).entity(blockedDate).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes({MediaType.APPLICATION_JSON})
	public Response updateBlockedDate(@PathParam("id") final Integer id, @RequestBody final Map<String, Object> data) throws Exception {
		checkAndGetBlockedDate(id);
		
		BlockedDate modifiedBlockedDate = new BlockedDate(id, (Integer) data.get("start"), (Integer) data.get("end"), securityContextFacade.getCurrentUserEmail());
		final Boolean updateSuccessful = blockedDatesRepository.updateBlockedDate(modifiedBlockedDate);
		if (!updateSuccessful) {
			throw new EmptyResultDataAccessException(1);
		}
		modifiedBlockedDate = blockedDatesRepository.getBlockedDate(modifiedBlockedDate.getId());

		return Response.status(Response.Status.OK).entity(modifiedBlockedDate).build();
	}
	
	@DELETE
	@Path("/{id}")
	public Response deleteBlockedDate(@PathParam("id") final Integer id) throws Exception {
		checkAndGetBlockedDate(id);

		blockedDatesRepository.deleteBlockedDate(id);

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	private BlockedDate checkAndGetBlockedDate(final Integer id) {
		final BlockedDate blockedDate = blockedDatesRepository.getBlockedDate(id);

		if(!blockedDate.getUserEmail().equals(securityContextFacade.getCurrentUserEmail())) {
			throw new AccessDeniedException("This date does not belong to the specified user.");
		}
		
		return blockedDate;
	}
}
