package com.likhith.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.likhith.gateway.dto.UserResponse;

@RestControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(TechnicalException.class)
	public ResponseEntity<UserResponse> technicalException(TechnicalException exception) {
		ErrorMessage errorMessage = new ErrorMessage(exception.getStatusCode(), exception.getErrorMessage());
		return ResponseEntity.status(errorMessage.getStatusCode()).body(new UserResponse(errorMessage));
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<UserResponse> validationException(ValidationException exception) {
		ErrorMessage errorMessage = new ErrorMessage(exception.getStatusCode(), exception.getErrorMessage());
		return ResponseEntity.status(errorMessage.getStatusCode()).body(new UserResponse(errorMessage));
	}

	@ExceptionHandler({ UsernameNotFoundException.class, BadCredentialsException.class,
			InsufficientAuthenticationException.class })
	public ResponseEntity<UserResponse> authenticationException(AuthenticationException exception) {
		ErrorMessage errorMessage = new ErrorMessage(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
		return ResponseEntity.status(errorMessage.getStatusCode()).body(new UserResponse(errorMessage));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<UserResponse> runtimeException(RuntimeException exception) {
		ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Unexpected Error occured. Please try again after sometime");
		return ResponseEntity.status(errorMessage.getStatusCode()).body(new UserResponse(errorMessage));
	}
}