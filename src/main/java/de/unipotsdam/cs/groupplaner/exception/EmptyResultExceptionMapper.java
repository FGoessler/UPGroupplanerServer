package de.unipotsdam.cs.groupplaner.exception;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This ExceptionMapper handles any EmptyResultDataAccessExceptions and maps them to a "404 Not Found" status code.
 * These exceptions occur e.g. if a sql request delivered zero results.
 */
@ControllerAdvice
public class EmptyResultExceptionMapper {

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(EmptyResultDataAccessException.class)
	public void toResponse() {

	}
}