package com.excilys.cdb.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.excilys.cdb.controllers.exception.CompanyUpdateNotExistingException;
import com.excilys.cdb.controllers.exception.ConflictUpdateException;
import com.excilys.cdb.exceptions.company.CompanyException;
import com.excilys.cdb.exceptions.company.CompanyUnknownException;
import com.excilys.cdb.exceptions.computer.ComputerException;
import com.excilys.cdb.exceptions.computer.ComputerNamelessException;
import com.excilys.cdb.exceptions.computer.ComputerNonExistentException;
import com.excilys.cdb.exceptions.computer.ComputerWithBadDatesException;
import com.excilys.cdb.model.ApiException;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {
	
	/**
	 * When a @Valid fails.
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, 
	  HttpHeaders headers, HttpStatus status, WebRequest request) {
		StringBuilder stringBuilder = new StringBuilder();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
	        stringBuilder.append((error.getField() + ": " + error.getDefaultMessage()));
	    }
	    ApiException apiException = new ApiException(ex.getMessage(), stringBuilder.toString());
	    return ResponseEntity.badRequest().body(apiException);
	}
	
	/**
	 * When there is a missing parameter in the request.
	 */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
	    String error = ex.getParameterName() + " parameter is missing";
	    ApiException apiException = new ApiException(ex.getMessage(), error);
	    return ResponseEntity.badRequest().body(apiException);
	}
	
	/**
	 * CompanyUnknownException handler
	 */
	@ExceptionHandler(value = { CompanyUnknownException.class })
	protected ResponseEntity<ApiException> handleConflict(CompanyException ex, WebRequest request) {
		ApiException apiException = new ApiException(ex.getMessage(), ex.getClass().getName());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiException);
	}
	
	/**
	 * CompanyUnknownException handler
	 */
	@ExceptionHandler(value = { ComputerNonExistentException.class })
	protected ResponseEntity<ApiException> handleConflict(ComputerNonExistentException ex, WebRequest request) {
		ApiException apiException = new ApiException(ex.getMessage(), ex.getClass().getName());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiException);
	}
	
	/**
	 * ComputerNamelessException handler
	 */
	@ExceptionHandler(value = { ComputerNamelessException.class, ComputerWithBadDatesException.class })
	protected ResponseEntity<ApiException> handleComputerMalformedEntities(ComputerException ex, WebRequest request) {
		ApiException apiException = new ApiException(ex.getMessage(), ex.getClass().getName());
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(apiException);
	}
	/**
	 * ComputerException handler
	 */
	@ExceptionHandler(value = { ComputerException.class })
	protected ResponseEntity<ApiException> handleComputerException(ComputerException ex, WebRequest request) {
		ApiException apiException = new ApiException(ex.getMessage(), ex.getClass().getName());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiException);
	}
	
	/**
	 * If we try to make an incorect update
	 * @param ex conflictUpdateException
	 * @return 409 : Conlict
	 */
	@ExceptionHandler(value = {ConflictUpdateException.class})
    protected ResponseEntity<ApiException> handleConflictUpdateException(final ConflictUpdateException ex) {
        final ApiException apiException = new ApiException(ex.getMessage(),ex.getClass().getName());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiException);
    }
	
	@ExceptionHandler(value = {CompanyUpdateNotExistingException.class})
	protected ResponseEntity<ApiException> handleCompanyUpdateNotExistingException(final CompanyUpdateNotExistingException ex){
	    final ApiException apiException = new ApiException(ex.getMessage(),ex.getClass().getName());
	    return ResponseEntity.status(HttpStatus.ACCEPTED).body(apiException);
	}
	
	/**
	 * Default exception handler.
	 */
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<ApiException> handleAll(Exception ex, WebRequest request) {
	    ApiException apiException = new ApiException(ex.getMessage(), ex.getClass().getName());
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiException);
	}
}
