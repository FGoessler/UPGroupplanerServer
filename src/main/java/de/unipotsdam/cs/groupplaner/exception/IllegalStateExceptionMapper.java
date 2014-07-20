package de.unipotsdam.cs.groupplaner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * This ExceptionMapper handles any IllegalStateException and maps them to a "412 PRECONDITION FAILED" status code.
 */
@ControllerAdvice
public class IllegalStateExceptionMapper {

	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	@ExceptionHandler(IllegalStateException.class)
	public Map<String, String> toResponse(IllegalStateException exception) {

		Map<String, String> errMsgMap = new HashMap<String, String>();
		if (exception.getLocalizedMessage() != null) {
			errMsgMap.put("message", exception.getLocalizedMessage());
		}

		return errMsgMap;
	}
}