package de.unipotsdam.cs.groupplaner.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
@Component
public class UnhandledExceptionMapper implements ExceptionMapper<Exception> {

	@Autowired
	private Logger logger;

	@Override
	public Response toResponse(Exception exception) {

		logger.log(Level.WARNING, exception.getMessage() + "\n" + getStackTrace(exception));
		
		Map<String, String> errMsgMap = new HashMap<String, String>();
		errMsgMap.put("message", exception.getLocalizedMessage());
		
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
				entity(errMsgMap).
				type(MediaType.APPLICATION_JSON).
				build();
	}
	
	private String getStackTrace(Exception exception) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter, true);
		exception.printStackTrace(printWriter);
		return stringWriter.getBuffer().toString();
	}
}
