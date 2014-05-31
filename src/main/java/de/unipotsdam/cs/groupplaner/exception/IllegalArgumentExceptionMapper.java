package de.unipotsdam.cs.groupplaner.exception;

import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * This ExceptionMapper handles any IllegalArgumentExceptions and maps them to a "409 CONFLICT" status code.
 */
@Provider
@Component
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

	@Override
	public Response toResponse(IllegalArgumentException exception) {

		Map<String, String> errMsgMap = new HashMap<String, String>();
		errMsgMap.put("message", exception.getLocalizedMessage());

		return Response.status(Response.Status.CONFLICT).
				entity(errMsgMap).
				type(MediaType.APPLICATION_JSON).
				build();
	}
}