package de.unipotsdam.cs.groupplaner.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * This ExceptionMapper converts any AccessDeniedExceptions to a "403 Forbidden" status code.
 */
@Provider
@Component
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

	@Override
	public Response toResponse(AccessDeniedException exception) {

		return Response.status(Response.Status.FORBIDDEN).
				type(MediaType.APPLICATION_JSON).
				build();
	}

}
