package de.unipotsdam.cs.groupplaner.auth;

import de.unipotsdam.cs.groupplaner.domain.User;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Autowire this class to get access to the spring security SecurityContext and to retrieve the current user.
 */
@Component
public class SecurityContextFacade {

	/**
	 * Gets the current user. Please note that the name property might be null since the user's data isn't looked up in 
	 * the database. Basically this only wraps the user's email from getCurrentUserEmail() in an User object. 
	 * @return The current user.
	 */
	public User getCurrentUser() {
		return new User(getCurrentUserEmail(),null);
	}
	
	public String getCurrentUserEmail() {
		return getSecurityContext().getAuthentication().getName();
	}
	
	public SecurityContext getSecurityContext() {
		return SecurityContextHolder.getContext();
	}
}
