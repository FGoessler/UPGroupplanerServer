package de.unipotsdam.cs.groupplaner.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This ExceptionMapper converts any CannotGetJdbcConnectionException, raised cause the MySQL DB could not be accessed,
 * to a "500 Internal Server Error" status code with a useful message.
 */
@Provider
@Component
public class CommunicationsExceptionMapper implements ExceptionMapper<CannotGetJdbcConnectionException> {

	@Autowired
	private Logger logger;

	@Override
	public Response toResponse(CannotGetJdbcConnectionException exception) {

		logger.log(Level.SEVERE, "Communication with database failed.\nCheck if you can access the database - you might need to be in the same VPN.\n" + exception.getMessage());

		Map<String, String> errMsgMap = new HashMap<String, String>();
		errMsgMap.put("message", "Communication with database failed.");

		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
				entity(errMsgMap).
				type(MediaType.APPLICATION_JSON).
				build();
	}

}
