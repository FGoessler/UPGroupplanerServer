package de.unipotsdam.cs.groupplaner.resource;

import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.User;
import de.unipotsdam.cs.groupplaner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path(PathConfig.BASE_RESOURCE_PATH)
@Produces({MediaType.APPLICATION_JSON})
public class UserResource {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SecurityContextFacade securityContextFacade;

	@GET
	public Response getUser() throws Exception {
		String email = securityContextFacade.getCurrentUserEmail();
		User user = userRepository.getUser(email);
		if (user == null) {
			userRepository.createUser(new User(email, ""));
			user = userRepository.getUser(email);
		}

		return Response.status(Response.Status.OK).entity(user).build();
	}
}
