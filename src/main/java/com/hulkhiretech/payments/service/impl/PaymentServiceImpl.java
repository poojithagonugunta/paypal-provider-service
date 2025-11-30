package com.hulkhiretech.payments.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.hhtp.HttpRequest;
import com.hulkhiretech.payments.hhtp.HttpServiceEngine;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.CreateOrderResponse;
import com.hulkhiretech.payments.service.TokenService;
import com.hulkhiretech.payments.service.ValidateCaptureOrder;
import com.hulkhiretech.payments.service.ValidateCreateOrder;
import com.hulkhiretech.payments.service.helper.CaptureOrderHelper;
import com.hulkhiretech.payments.service.helper.CreateOrderHelper;
import com.hulkhiretech.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {


	private final TokenService tokenService;

	private final ObjectMapper mapper;

	private final HttpServiceEngine hhtpServiceEngine;


	private final  CreateOrderHelper  createOrderHelper;


	private final ValidateCreateOrder validateCreateOrder;

	private final ValidateCaptureOrder validateCaptureOrder;

	private final  CaptureOrderHelper  captureOrderHelper;
	

	@Override
	public CreateOrderResponse createOrder(CreateOrderReq createOrderReq) {
		log.info("creating order in payment service Impl");
		/*TODO
		1.getAccessToken (OAuth)
		2.Call paypal createOrder
		3.Success/Failure/TimeOut - Proper response handling
		4.What to return to your calling service (payment-processing-service)

		 */
		validateCreateOrder.vadilateCreateOrderRequest(createOrderReq);

		log.info("the validation createorder request is succesful");

		//get access token
		String accessToken=tokenService.getAccessToken();

		HttpRequest request = createOrderHelper.prepareCreateHttpRequest(createOrderReq, accessToken);

		log.info("HttpRequest prepared to call hhatpservice engine make api call {}",request);

		// sending our request to make hhtp call
		ResponseEntity<String> hhtpResponse= hhtpServiceEngine.makeHttpCall(request);

		log.info("retrived response body from hhtpservice engine{}",hhtpResponse);

		//converting my response into java object in order to get our required fields
		CreateOrderResponse response= createOrderHelper.handlePaypalResponse(hhtpResponse);
		return response ;

	}
	
	
	@Override
	public CreateOrderResponse captureOrder(String orderId) {
		log.info("capturing order in payment service Impl with orderId: {} ",orderId);
		validateCaptureOrder.vadilateCaptureOrderRequest(orderId);
		log.info("the validation capture order request is succesful");
		//get access token
		String accessToken=tokenService.getAccessToken();

		HttpRequest request = captureOrderHelper.prepareCaptureHttpRequest(orderId, accessToken);
		log.info("HttpRequest prepared to call hhatpservice engine make api call {}",request);
		// sending our request to make hhtp call
		ResponseEntity<String> hhtpCaptureResponse= hhtpServiceEngine.makeHttpCall(request);
		log.info("retrived response body from hhtpservice engine for capture order {}",hhtpCaptureResponse);
		

		CreateOrderResponse response=captureOrderHelper.createCaptureOrderResponse(hhtpCaptureResponse,orderId);
		return response;

	}
	

}
