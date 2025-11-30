package com.hulkhiretech.payments.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaypalProviderException;
@Service
public class ValidateCaptureOrder {
	
	public void vadilateCaptureOrderRequest(String orderId) {
		
		//check wheather the order id is null or blank
		if(orderId==null || orderId.isBlank()) {
			throw new PaypalProviderException(
					ErrorCodeEnum.ORDERID.getCode(),
					ErrorCodeEnum.ORDERID.getMessage(),HttpStatus.BAD_REQUEST);
		}
		
		
	
		
	}

}
