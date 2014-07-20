package de.unipotsdam.cs.groupplaner.user.resource;

import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.User;
import de.unipotsdam.cs.groupplaner.user.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserResource {

	@Autowired
	private UserDAO userDAO;
	@Autowired
	private SecurityContextFacade securityContextFacade;

	@RequestMapping(PathConfig.BASE_RESOURCE_PATH)
	public User getUser() {
		String email = securityContextFacade.getCurrentUserEmail();
		User user = userDAO.getUser(email);
		if (user == null) {
			userDAO.createUser(new User(email, ""));
			user = userDAO.getUser(email);
		}

		return user;
	}
}
