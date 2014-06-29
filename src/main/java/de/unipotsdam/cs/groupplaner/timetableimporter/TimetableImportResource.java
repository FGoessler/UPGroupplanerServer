package de.unipotsdam.cs.groupplaner.timetableimporter;

import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path(PathConfig.TIMETABLE_IMPORT_RESOURCE_PATH)
@Produces({MediaType.APPLICATION_JSON})
public class TimetableImportResource {

	@Autowired
	private SecurityContextFacade securityContextFacade;
	@Autowired
	private TimetableImportService timetableImportService;

	@GET
	public Response getUser() {
		final String userEmail = securityContextFacade.getCurrentUserEmail();
		final String password = securityContextFacade.getSecurityContext().getAuthentication().getCredentials().toString();

		timetableImportService.importUsersTimetableFromPULS(userEmail, password);

		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
