package de.unipotsdam.cs.groupplaner.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This ExceptionMapper converts any CannotGetJdbcConnectionException, raised cause the MySQL DB could not be accessed,
 * to a "500 Internal Server Error" status code with a useful message.
 */
@ControllerAdvice
public class CommunicationsExceptionMapper {

	@Autowired
	private Logger logger;

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(CannotGetJdbcConnectionException.class)
	public Map<String, String> toResponse(CannotGetJdbcConnectionException exception) {

		logger.log(Level.SEVERE, "Communication with database failed.\nCheck if you can access the database - you might need to be in the same VPN.\n" + exception.getMessage());

		Map<String, String> errMsgMap = new HashMap<String, String>();
		errMsgMap.put("message", "Communication with database failed.");

		return errMsgMap;
	}

}
