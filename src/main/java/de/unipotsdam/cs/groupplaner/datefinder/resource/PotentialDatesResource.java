package de.unipotsdam.cs.groupplaner.datefinder.resource;

import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.datefinder.service.PotentialDatesService;
import de.unipotsdam.cs.groupplaner.domain.dates.PrioritizedDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
@Path(PathConfig.POTENTIAL_DATES_RESOURCE_PATH)
@Produces({MediaType.APPLICATION_JSON})
public class PotentialDatesResource {

	@Autowired
	private PotentialDatesService potentialDatesService;

	@GET
	public Response getPotentialDates(@PathParam("id") final Integer groupId) {
		final List<PrioritizedDate> potentialDates = potentialDatesService.calculatePotentialDates(groupId);
		return Response.status(Response.Status.OK).entity(potentialDates).build();
	}

}
