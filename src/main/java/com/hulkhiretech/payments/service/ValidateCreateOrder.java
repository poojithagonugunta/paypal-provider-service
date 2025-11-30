package com.hulkhiretech.payments.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaypalProviderException;
import com.hulkhiretech.payments.pojo.CreateOrderReq;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ValidateCreateOrder {

	public void vadilateCreateOrderRequest(CreateOrderReq createOrderReq) {
		if(createOrderReq==null) {
			log.info("create order request is null ");

			throw new PaypalProviderException(
					ErrorCodeEnum.INVALID_REQUEST.getCode(),ErrorCodeEnum.INVALID_REQUEST.getMessage(),HttpStatus.BAD_REQUEST);
		}


		if(createOrderReq.getCurrencyCode()==null|| createOrderReq.getCurrencyCode().isBlank()) {

			log.info("hey error occured as the there is no value sent in currency code");

			throw new PaypalProviderException(ErrorCodeEnum.CURRENCY_CODE_REQUIRED.getCode(),

					ErrorCodeEnum.CURRENCY_CODE_REQUIRED.getMessage(), HttpStatus.BAD_REQUEST);

		}

		if(createOrderReq.getAmount()==null||createOrderReq.getAmount()<0) {
			log.info("Enter a valid amount it cannot  be null less than 0 ",createOrderReq.getAmount());

			throw new PaypalProviderException(
					ErrorCodeEnum.INVALID_AMOUNT.getCode(),ErrorCodeEnum.INVALID_AMOUNT.getMessage(),HttpStatus.BAD_REQUEST);
		}

		if(createOrderReq.getReturnUrl()==null||createOrderReq.getReturnUrl().isBlank()) {
			log.info("return url is null ");

			throw new PaypalProviderException(
					ErrorCodeEnum.RETURN_URL_REQUIRED.getCode(),ErrorCodeEnum.RETURN_URL_REQUIRED.getMessage(),HttpStatus.BAD_REQUEST);
		}


		if(createOrderReq.getCancelUrl()==null||createOrderReq.getCancelUrl().isBlank()) {
			log.info("cancel url is null ");

			throw new PaypalProviderException(
					ErrorCodeEnum.CANCEL_URL_REQUIRED.getCode(),ErrorCodeEnum.CANCEL_URL_REQUIRED.getMessage(),HttpStatus.BAD_REQUEST);
		}



	}


}
