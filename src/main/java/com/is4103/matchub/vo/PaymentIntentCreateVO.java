package com.is4103.matchub.vo;

import com.is4103.matchub.enumeration.PaymentScenario;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentIntentCreateVO {

    @NotNull(message = "Amount can not be null.")
    private Long amountInCents;

    @NotNull(message = "Payee's Stripe account can not be null.")
    @NotBlank(message = "Payee's Stripe account can not be blank.")
    private String payeeStripeUid;

    private String payerStripeUid;

    
   // compulsory if payment is for resource purchase
    private Long resourceId;

    // compulsory if payment is for donation
    private Long donationOptionId;
    
   
    
    @NotNull(message = "payment scenario can not be null.")
    private PaymentScenario paymentScenario;
}
