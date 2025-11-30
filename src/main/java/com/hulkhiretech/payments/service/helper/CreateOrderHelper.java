package com.hulkhiretech.payments.service.helper;

import java.util.Collections;
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
import com.hulkhiretech.payments.paypal.req.Amount;
import com.hulkhiretech.payments.paypal.req.ExperienceContext;
import com.hulkhiretech.payments.paypal.req.OrderRequest;
import com.hulkhiretech.payments.paypal.req.PaymentSource;
import com.hulkhiretech.payments.paypal.req.Paypal;
import com.hulkhiretech.payments.paypal.req.PurchaseUnits;
import com.hulkhiretech.payments.paypal.res.CreateOrder;
import com.hulkhiretech.payments.paypal.res.PaypalLinks;
import com.hulkhiretech.payments.paypal.res.error.PaypalErrorResponse;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.CreateOrderResponse;
import com.hulkhiretech.payments.utils.JsonUtils;
import com.hulkhiretech.payments.utils.PaypalOrderUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrderHelper {

	
	@Value("${paypal-create-order-url}")
	private String createOrderUrl;

	private final JsonUtils jsonUtils;
	
	public  HttpRequest prepareCreateHttpRequest(CreateOrderReq createOrderReq, String accessToken) {
		HttpHeaders headers = new HttpHeaders();

		//creating the header 
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		String uuid=UUID.randomUUID().toString();
		headers.add(Constant.PAY_PAL_REQUEST_ID, uuid);

		
		log.info("header was prepared {}",headers);
		//preparing the request body

		Amount amount =new Amount();
		amount.setCurrencyCode(createOrderReq.getCurrencyCode());
		String formatted = String.format(Constant.TWO_DECIMAL_VALUE, createOrderReq.getAmount());
		amount.setValue(formatted);

		PurchaseUnits purchaseUnits=new PurchaseUnits();
		purchaseUnits.setAmount(amount);



		ExperienceContext experienceContext=new ExperienceContext();
		experienceContext.setPaymentMethodPreference(Constant.IMMEDIATE_PAYMENT_REQUIRED);
		experienceContext.setShippingPreference(Constant.SHIPPING_PREF);
		experienceContext.setLandingPage(Constant.LANDING_PAGE);
		experienceContext.setUserAction(Constant.USER_ACTION);
		experienceContext.setReturnUrl(createOrderReq.getReturnUrl());
		experienceContext.setCancelUrl(createOrderReq.getCancelUrl());

		Paypal paypal=new Paypal();
		paypal.setExperienceContext(experienceContext);

		PaymentSource paymentSource=new PaymentSource();
		paymentSource.setPaypal(paypal);

		OrderRequest orderRequest=new OrderRequest();
		orderRequest.setIntent(Constant.INTENT_CAPTURE);
		orderRequest.setPaymentSource(paymentSource);
		orderRequest.setPurchaseUnits(Collections.singletonList(purchaseUnits));
		
		log.info("the request body was prepared {}",orderRequest);
		String requestJson= jsonUtils.toJson(orderRequest);

		//create hhtp request in order to make the  hhtpcall 

		HttpRequest request=new HttpRequest();
		request.setHhtpMethod(HttpMethod.POST);
		request.setUrl(createOrderUrl);
		request.setHhtpHeaders(headers);
		request.setBody(requestJson);
		
		log.info("hhtprequest prepared and returning it{}",request);
		return request;
	}
	
	public CreateOrderResponse prepaeCreateOrderResponse(CreateOrder createOrder) {
		//setting the value to our java object and return that as response
		CreateOrderResponse createOrderResponse=new CreateOrderResponse();

		createOrderResponse.setOrderId(createOrder.getId());
		createOrderResponse.setOrderStatus(createOrder.getStatus());

		 String redirectLink = createOrder.getLinks().stream()
		            .filter(link -> "payer-action".equalsIgnoreCase(link.getRel()))
		            .findFirst()
		            .map(PaypalLinks::getHref)
		            .orElse(null);

		createOrderResponse.setRedirectUrl(redirectLink);
		return createOrderResponse;
	}
	
public CreateOrderResponse handlePaypalResponse(ResponseEntity<String> httpResponse) {
		log.info("handling paypalresponse  in paymentserviceimpl {}",httpResponse);
		if(httpResponse.getStatusCode().is2xxSuccessful()) {
			
			CreateOrder createOrder=jsonUtils.fromJson(httpResponse.getBody(),CreateOrder.class);
			log.info("converted json string which is returned from paypal api to createOrder Object {}",createOrder);
			
			CreateOrderResponse createOrderResponse = prepaeCreateOrderResponse(createOrder);	

			// If we get a valid response with PAYER_ACTION_REQUIRED status & url & id,
			if(createOrderResponse != null
			    && createOrderResponse.getOrderId() != null
			    && !createOrderResponse.getOrderId().isEmpty()
			    && createOrderResponse.getOrderStatus() != null
			    && createOrderResponse.getOrderStatus().equalsIgnoreCase(
			        Constant.PAYER_ACTION_REQUIRED)
			    && createOrderResponse.getRedirectUrl() != null
			    && !createOrderResponse.getRedirectUrl().isEmpty()) {
			    log.info("Order created successfully with PAYER_ACTION_REQUIRED status");
			    return createOrderResponse;
			}
			
			
	
		}
		
		
		if(httpResponse.getStatusCode().is4xxClientError()||httpResponse.getStatusCode().is5xxServerError()) {
			log.error("received 4xx and 5XX error response from paypal service");
			
			PaypalErrorResponse paypalErrorResponse=jsonUtils.fromJson(httpResponse.getBody(),PaypalErrorResponse.class);
			
			log.info("paypal error reponse details {} ",paypalErrorResponse);
			
			String errorCode = ErrorCodeEnum.PAYPAL_ERROR.getCode();
			String errorMessage = PaypalOrderUtils.getPaypalErrorSummary(paypalErrorResponse);//TODO

			throw new PaypalProviderException(
			    errorCode,
			    errorMessage,
			    HttpStatus.valueOf(httpResponse.getStatusCode().value()));
			
		}
		
		log.error("Unexpected response from PayPal service. "
		        + "httpResponse: {}", httpResponse);

		throw new PaypalProviderException(
		        ErrorCodeEnum.PAYPAL_UNKNOWN_ERROR.getCode(),
		        ErrorCodeEnum.PAYPAL_UNKNOWN_ERROR.getMessage(),
		        HttpStatus.BAD_GATEWAY);
		


	}


}
