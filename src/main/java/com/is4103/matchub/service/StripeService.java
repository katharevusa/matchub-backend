package com.is4103.matchub.service;

import com.is4103.matchub.exception.DonationOptionNotFoundException;
import com.is4103.matchub.vo.PaymentIntentCreateVO;
import com.stripe.model.AccountLink;
import com.stripe.model.Balance;
import com.stripe.model.LoginLink;
import com.stripe.model.PaymentIntent;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author tjle2
 */
public interface StripeService {

    public AccountLink createStripeAccount(String email);

    public PaymentIntent createPaymentIntent(PaymentIntentCreateVO paymentIntentCreateVO);

    public String handleWebhookEvent(String json, HttpServletRequest request)throws DonationOptionNotFoundException;

    public LoginLink getStripeExpressDashboard(String stripeAccountUid);

    public Balance getStripeBalanceByStripeAccount(String stripeAccountUid);
}
