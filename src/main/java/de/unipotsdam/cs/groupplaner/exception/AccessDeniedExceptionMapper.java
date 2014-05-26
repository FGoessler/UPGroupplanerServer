package de.unipotsdam.cs.groupplaner.exception;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class AccessDeniedExceptionMapper implements ExceptionMapper<EmptyResultDataAccessException> {

	@Override
	public Response toResponse(EmptyResultDataAccessException exception) {

		return Response.status(Response.Status.FORBIDDEN).
				type(MediaType.APPLICATION_JSON).
				build();
	}

}
