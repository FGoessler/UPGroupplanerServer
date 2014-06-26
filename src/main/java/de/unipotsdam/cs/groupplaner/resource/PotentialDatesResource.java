package de.unipotsdam.cs.groupplaner.resource;

import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.PeriodDate;
import de.unipotsdam.cs.groupplaner.service.PotentialDatesService;
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
		final List<PeriodDate> potentialDates = potentialDatesService.calculatePotentialDates(groupId);
		return Response.status(Response.Status.OK).entity(potentialDates).build();
	}

}
