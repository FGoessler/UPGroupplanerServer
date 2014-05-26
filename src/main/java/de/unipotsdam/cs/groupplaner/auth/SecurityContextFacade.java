package de.unipotsdam.cs.groupplaner.auth;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextFacade {
	
	public String getCurrentUserEmail() {
		return getSecurityContext().getAuthentication().getName();
	}
	
	public SecurityContext getSecurityContext() {
		return SecurityContextHolder.getContext();
	}
}
