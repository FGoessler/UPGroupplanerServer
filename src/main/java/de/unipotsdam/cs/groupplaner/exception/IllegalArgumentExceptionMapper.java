package de.unipotsdam.cs.groupplaner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * This ExceptionMapper handles any IllegalArgumentExceptions and maps them to a "409 CONFLICT" status code.
 */
@ControllerAdvice
public class IllegalArgumentExceptionMapper {

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(IllegalArgumentException.class)
	public Map<String, String> toResponse(IllegalArgumentException exception) {

		Map<String, String> errMsgMap = new HashMap<String, String>();
		if (exception.getLocalizedMessage() != null) {
			errMsgMap.put("message", exception.getLocalizedMessage());
		}

		return errMsgMap;
	}
}