package com.hulkhiretech.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionalHandler {
	
	@ExceptionHandler(PaypalProviderException.class)
	public ResponseEntity<ErrorResponse> handlePaypalException(PaypalProviderException e){
		log.info("handling paypal provider exception {}",e.getErrorMeesage(),e);
		ErrorResponse response=new ErrorResponse(e.getErrorCode(),e.getErrorMeesage());
		
		return ResponseEntity.status(e.getHhtpStatus()).body(response);
		
	}
	
	// NoResourceFoundException
		@ExceptionHandler(NoResourceFoundException.class)
		public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
				NoResourceFoundException ex) {
			log.error("Handling NoResourceFoundException: {}", ex.getMessage(), ex);
			
			ErrorResponse error = new ErrorResponse(
					ErrorCodeEnum.RESOURCE_NOT_FOUND.getCode(), 
					ErrorCodeEnum.RESOURCE_NOT_FOUND.getMessage());
			
			return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); 
		}
		
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handlePaypalException(Exception e){
		log.info("handling paypal provider exception {}",e);
		ErrorResponse response=new ErrorResponse(ErrorCodeEnum.GENERIC_ERROR.getCode(),
				
				ErrorCodeEnum.GENERIC_ERROR.getMessage());
		
		return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
}
