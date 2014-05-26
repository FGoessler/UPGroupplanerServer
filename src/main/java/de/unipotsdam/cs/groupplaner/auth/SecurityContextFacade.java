package de.unipotsdam.cs.groupplaner.auth;

import de.unipotsdam.cs.groupplaner.domain.User;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextFacade {
	
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
