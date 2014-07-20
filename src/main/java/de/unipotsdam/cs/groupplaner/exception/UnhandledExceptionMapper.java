package de.unipotsdam.cs.groupplaner.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This ExceptionMapper handles any unmapped exceptions and provides an appropiate msg in JSON/XML format.
 */
@ControllerAdvice
public class UnhandledExceptionMapper {

	@Autowired
	private Logger logger;

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public Map<String, String> toResponse(Exception exception) {

		logger.log(Level.WARNING, exception.getMessage() + "\n" + getStackTrace(exception));

		Map<String, String> errMsgMap = new HashMap<String, String>();
		if (exception.getLocalizedMessage() != null) {
			errMsgMap.put("message", exception.getLocalizedMessage());
		}

		return errMsgMap;
	}

	private String getStackTrace(Exception exception) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter, true);
		exception.printStackTrace(printWriter);
		return stringWriter.getBuffer().toString();
	}
}
