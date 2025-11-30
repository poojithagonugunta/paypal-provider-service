package com.hulkhiretech.payments.paypal.res.error;


import lombok.Data;

@Data
public class Link {
    private String href;
    private String rel;
    private String encType;
}
