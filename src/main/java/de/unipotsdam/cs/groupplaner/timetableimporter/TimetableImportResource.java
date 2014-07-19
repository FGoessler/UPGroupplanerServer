package de.unipotsdam.cs.groupplaner.timetableimporter;

import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;

@RestController
public class TimetableImportResource {

	@Autowired
	private SecurityContextFacade securityContextFacade;
	@Autowired
	private TimetableImportService timetableImportService;

	@RequestMapping(PathConfig.TIMETABLE_IMPORT_RESOURCE_PATH)
	public Response getUser() {
		final String userEmail = securityContextFacade.getCurrentUserEmail();
		final String password = securityContextFacade.getSecurityContext().getAuthentication().getCredentials().toString();

		timetableImportService.importUsersTimetableFromPULS(userEmail, password);

		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
