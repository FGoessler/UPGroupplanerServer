package de.unipotsdam.cs.groupplaner.exception;

import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * This ExceptionMapper handles any EmptyResultDataAccessExceptions and maps them to a "404 Not Found" status code.
 * These exceptions occur e.g. if a sql request delivered zero results.
 */
@Provider
@Component
public class IllegalStateExceptionMapper implements ExceptionMapper<IllegalStateException> {

	@Override
	public Response toResponse(IllegalStateException exception) {

		Map<String, String> errMsgMap = new HashMap<String, String>();
		errMsgMap.put("message", exception.getLocalizedMessage());

		return Response.status(Response.Status.PRECONDITION_FAILED).
				entity(errMsgMap).
				type(MediaType.APPLICATION_JSON).
				build();
	}
}