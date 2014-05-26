package de.unipotsdam.cs.groupplaner.resource;

import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Component
@Path("/")
public class TestResource {

	@Autowired
	private SecurityContextFacade securityContextFacade;
	
	@GET
	public Response test() {
		final String username = securityContextFacade.getCurrentUserEmail();

		return Response.status(200).entity("System running... \nWelcome " + username + "!").build();
	}
	
}
