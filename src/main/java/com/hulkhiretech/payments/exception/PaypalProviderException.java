package com.hulkhiretech.payments.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class PaypalProviderException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String errorCode;
	
	private final String errorMeesage;
	
	private final HttpStatus hhtpStatus;
	
	public PaypalProviderException(String errorCode, String errorMessage, HttpStatus hhtpStatus){
		super(errorMessage);
		this.errorCode=errorCode;
		this.errorMeesage=errorMessage;
		this.hhtpStatus=hhtpStatus;
	}

}
