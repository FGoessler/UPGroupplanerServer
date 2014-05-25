package de.unipotsdam.cs.groupplaner.resource;

import de.unipotsdam.cs.groupplaner.domain.User;
import de.unipotsdam.cs.groupplaner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/user/{email}")
public class UserResource {
	
	@Autowired
	private UserRepository userRepository;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getUser(@PathParam("email") final String email) throws Exception {
		User user = userRepository.getUser(email);
		if(user == null) {
			userRepository.createUser(new User(email, ""));
			user = userRepository.getUser(email);
		}
		
		return Response.status(200).entity(user).build();
	}
}
