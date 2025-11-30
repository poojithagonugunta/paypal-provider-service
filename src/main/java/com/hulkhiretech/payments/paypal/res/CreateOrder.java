package com.hulkhiretech.payments.paypal.res;



import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CreateOrder {
   
	
    private String id;
    private String status;
    
    @JsonProperty("payment_source")
    private PaymentSource paymentSource;
    
   
    private List<PaypalLinks> links;
	
	
	
}
