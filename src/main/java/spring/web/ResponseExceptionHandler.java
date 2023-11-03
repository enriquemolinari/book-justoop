package spring.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import model.api.AuthException;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleAllMyException(
			Exception ex,
			WebRequest request) {

		return new ResponseEntity<String>(
				ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(AuthException.class)
	public ResponseEntity<String> handleAllBusinessExceptions(
			Exception ex,
			WebRequest request) {

		return new ResponseEntity<String>(
				ex.getMessage(),
				HttpStatus.UNAUTHORIZED);
	}
}
