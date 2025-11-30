package com.hulkhiretech.payments.service.interfaces;


import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.CreateOrderResponse;

public interface PaymentService {

	public CreateOrderResponse createOrder(CreateOrderReq createOrderReq);

	public CreateOrderResponse captureOrder(String orderId);
}
