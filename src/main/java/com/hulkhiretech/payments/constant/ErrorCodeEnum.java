package com.hulkhiretech.payments.constant;

import lombok.Getter;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    
    CURRENCY_CODE_REQUIRED("3001", "Currency code is required and cannot be null/blank"),
    GENERIC_ERROR("3000", "Something went wrong, please try again!"),
    INVALID_REQUEST("30002","Invalid request please enter valid request"),
	INVALID_AMOUNT("30003","Enter a valid  Amount for transaction"),
	CANCEL_URL_REQUIRED("30004","cancel url cannot be null or blank"),
	RETURN_URL_REQUIRED("30005","Return url cannot be null or blank"), 
	PAYPAL_SERVICE_UNAVAILABLE("30006","Paypal server is currently unavailable please try again later"),
	PAYPAL_ERROR("3007","<Error At paypal>"),
	PAYPAL_UNKNOWN_ERROR("3008","Unknown error occurred at paypal"),
	ORDERID("3009","OrderId cannot be null or blank"),
	RESOURCE_NOT_FOUND("30009", "Invalid URL. Please check and try again.");
	
	

    private final String code;
    private final String message;

    ErrorCodeEnum(String code, String message) {
        this.code = code;
        
        this.message = message;
    }
}
