package de.unipotsdam.cs.groupplaner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This ExceptionMapper converts any AccessDeniedExceptions to a "403 Forbidden" status code.
 */
@ControllerAdvice
public class AccessDeniedExceptionMapper {

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(AccessDeniedException.class)
	public void toResponse() {

	}

}
