package com.is4103.matchub.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentIntentCreateVO {

    @NotNull(message = "Amounct can not be null.")
    private Long amountInCents;

    @NotNull(message = "Payee's Stripe account can not be null.")
    @NotBlank(message = "Payee's Stripe account can not be blank.")
    private String payeeStripeUid;

    private String payerStripeUid;

    // not sure if these are correct, change at own discretion
    // so that can use to find and associate later on
    private Long resourceId;

    // so that can use to find and associate later on
    private Long donationOptionId;
}
