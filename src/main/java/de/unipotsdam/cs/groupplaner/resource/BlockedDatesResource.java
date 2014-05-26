package de.unipotsdam.cs.groupplaner.resource;

import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.BlockedDate;
import de.unipotsdam.cs.groupplaner.repository.BlockedDatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Component
@Path(PathConfig.BLOCKED_DATES_RESOURCE_PATH)
public class BlockedDatesResource {

	@Autowired
	private BlockedDatesRepository blockedDatesRepository;
	@Autowired
	private SecurityContextFacade securityContextFacade;

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllBlockedDates() throws Exception {
		final ImmutableList<BlockedDate> blockedDates = blockedDatesRepository.getBlockedDates(securityContextFacade.getCurrentUserEmail());

		return Response.status(200).entity(blockedDates).build();
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response createBlockedDate(@RequestBody final Map<String, Object> data) throws Exception {
		BlockedDate newBlockedDate = new BlockedDate((Integer)data.get("start"), (Integer)data.get("end"), (String)data.get("userEmail"));
		
		blockedDatesRepository.createBlockedDate(newBlockedDate);
		
		return Response.status(201).build();
	}

	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getBlockedDate(@PathParam("id") final Integer id) throws Exception {
		final BlockedDate blockedDate = blockedDatesRepository.getBlockedDate(id);
		
		if(!blockedDate.getUserEmail().equals(securityContextFacade.getCurrentUserEmail())) {
			throw new AccessDeniedException("This date does not belong to the specified user.", null);
		}

		return Response.status(200).entity(blockedDate).build();
	}

	@DELETE
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteBlockedDate(@PathParam("id") final Integer id) throws Exception {
		blockedDatesRepository.deleteBlockedDate(id);

		return Response.status(200).build();
	}
}
