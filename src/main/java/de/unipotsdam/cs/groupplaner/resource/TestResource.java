package de.unipotsdam.cs.groupplaner.resource;

import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Component
@Path("/")
public class TestResource {
	
	@GET
	public Response test() {
		return Response.status(200).entity("System running...").build();
	}
	
}
