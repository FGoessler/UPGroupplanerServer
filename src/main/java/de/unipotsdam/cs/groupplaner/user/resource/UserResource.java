package de.unipotsdam.cs.groupplaner.user.resource;

import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.User;
import de.unipotsdam.cs.groupplaner.user.dao.UserDAO;
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
	private UserDAO userDAO;
	@Autowired
	private SecurityContextFacade securityContextFacade;

	@GET
	public Response getUser() {
		String email = securityContextFacade.getCurrentUserEmail();
		User user = userDAO.getUser(email);
		if (user == null) {
			userDAO.createUser(new User(email, ""));
			user = userDAO.getUser(email);
		}

		return Response.status(Response.Status.OK).entity(user).build();
	}
}
