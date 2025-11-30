package com.hulkhiretech.payments.service.helper;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaypalProviderException;
import com.hulkhiretech.payments.hhtp.HttpRequest;
import com.hulkhiretech.payments.paypal.res.CreateOrder;
import com.hulkhiretech.payments.paypal.res.error.PaypalErrorResponse;
import com.hulkhiretech.payments.pojo.CreateOrderResponse;
import com.hulkhiretech.payments.utils.JsonUtils;
import com.hulkhiretech.payments.utils.PaypalOrderUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaptureOrderHelper {


	

	@Value("${paypal-capture-order-url}")
	private String captureUrl;
	
	private final JsonUtils jsonUtils;

	public  HttpRequest prepareCaptureHttpRequest(String orderId,String accessToken) {
		//creating the header 

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		String uuid=UUID.randomUUID().toString();
		headers.add(Constant.PAY_PAL_REQUEST_ID, uuid);

		//preparing the url
		
		String url=captureUrl.replace(Constant.ORDER_ID, orderId);
		HttpRequest request=new HttpRequest();
		request.setHhtpMethod(HttpMethod.POST);
		request.setUrl(url);
		request.setHhtpHeaders(headers);
		request.setBody(Constant.EMPTY_BODY);

		return request;
	}
	
	public CreateOrderResponse toOrderResponse(CreateOrder createOrder) {
		log.info("Converting PaypalOrder to OrderResponse: {}", createOrder);
		
	    CreateOrderResponse response = new CreateOrderResponse();
	    response.setOrderId(createOrder.getId());
	    response.setOrderStatus(createOrder.getStatus());
	    	    
	    log.info("Converted PaypalOrder to OrderResponse: {}", response);
	    return response;
	}

	
	public CreateOrderResponse createCaptureOrderResponse(ResponseEntity<String> hhtpCaptureResponse, String orderId) {
		log.info("handling paypalresponse  in paymentserviceimpl {} and with orderId: {}",hhtpCaptureResponse,orderId);
		if(hhtpCaptureResponse.getStatusCode().is2xxSuccessful()) {
			CreateOrder paypalOrder = jsonUtils.fromJson(
					hhtpCaptureResponse.getBody(), CreateOrder.class);
			log.info("Converted response body to PaypalOrder: {}", paypalOrder);
			
			CreateOrderResponse orderResponse = toOrderResponse(paypalOrder);
			log.info("Converted OrderResponse: {}", orderResponse);
			
			if(orderResponse != null 
					&& orderResponse.getOrderId() != null
					&& !orderResponse.getOrderId().isEmpty()
					&& orderResponse.getOrderStatus() != null
					&& !orderResponse.getOrderStatus().isEmpty()) {
				log.info("Order capture successful. "
						+ "orderResponse: {}", orderResponse);
				return orderResponse;
			} 

			log.error("Order creation failed or incomplete details received. "
					+ "orderResponse: {}", orderResponse);	
	}
		if(hhtpCaptureResponse.getStatusCode().is4xxClientError()||hhtpCaptureResponse.getStatusCode().is5xxServerError()) {
			log.error("received 4xx and 5XX error response from paypal service");
			
			PaypalErrorResponse paypalErrorResponse=jsonUtils.fromJson(hhtpCaptureResponse.getBody(),PaypalErrorResponse.class);
			
			log.info("paypal error reponse details {} ",paypalErrorResponse);
			
			String errorCode = ErrorCodeEnum.PAYPAL_ERROR.getCode();
			String errorMessage = PaypalOrderUtils.getPaypalErrorSummary(paypalErrorResponse);//TODO

			throw new PaypalProviderException(
			    errorCode,
			    errorMessage,
			    HttpStatus.valueOf(hhtpCaptureResponse.getStatusCode().value()));
			
		}
		
		log.error("Unexpected response from PayPal service. "
		        + "httpResponse: {}", hhtpCaptureResponse);

		throw new PaypalProviderException(
		        ErrorCodeEnum.PAYPAL_UNKNOWN_ERROR.getCode(),
		        ErrorCodeEnum.PAYPAL_UNKNOWN_ERROR.getMessage(),
		        HttpStatus.BAD_GATEWAY);
	
}
}
