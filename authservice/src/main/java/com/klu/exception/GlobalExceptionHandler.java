package com.klu.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(404, "NOT_FOUND", ex.getMessage()));
	}

	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<ErrorResponse> handleBusiness(BusinessRuleException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse(409, "BUSINESS_RULE_VIOLATION", ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		String msg = ex.getBindingResult().getFieldErrors().stream()
				.map(e -> e.getField() + ": " + e.getDefaultMessage())
				.collect(Collectors.joining(", "));
		return ResponseEntity.badRequest().body(new ErrorResponse(400, "VALIDATION_FAILED", msg));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleOther(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse(500, "INTERNAL_ERROR", ex.getMessage()));
	}
}
