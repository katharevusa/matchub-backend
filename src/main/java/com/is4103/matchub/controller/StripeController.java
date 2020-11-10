package com.is4103.matchub.controller;

import com.is4103.matchub.service.StripeService;
import com.is4103.matchub.vo.PaymentIntentCreateVO;
import com.stripe.model.AccountLink;
import com.stripe.model.Balance;
import com.stripe.model.LoginLink;
import com.stripe.model.PaymentIntent;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tjle2
 */
@RestController
@RequestMapping("/authenticated")
public class StripeController {

    @Autowired
    StripeService stripeService;

    @RequestMapping(method = RequestMethod.POST, value = "/createStripeAccount")
    public ResponseEntity<?> createStripeAccount(@RequestParam(value = "email", required = true) String email) {
        AccountLink accountLink = stripeService.createStripeAccount(email);
        return new ResponseEntity<>(accountLink.toJson(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/createPaymentIntent")
    public ResponseEntity<?> createPaymentIntent(@Valid @RequestBody PaymentIntentCreateVO paymentIntentCreateVO) {
        PaymentIntent paymentIntent = stripeService.createPaymentIntent(paymentIntentCreateVO);
        return new ResponseEntity<>(paymentIntent.toJson(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getStripeExpressDashboard")
    public ResponseEntity<?> getStripeExpressDashboard(@RequestParam(value = "stripeAccountUid", required = true) String stripeAccountUid) {
        LoginLink loginLink = stripeService.getStripeExpressDashboard(stripeAccountUid);
        return new ResponseEntity<>(loginLink.toJson(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getStripeBalanceByStripeAccount")
    public ResponseEntity<?> getStripeBalanceByStripeAccount(@RequestParam(value = "stripeAccountUid", required = true) String stripeAccountUid) {
        Balance balance = stripeService.getStripeBalanceByStripeAccount(stripeAccountUid);
        return new ResponseEntity<>(balance.toJson(), HttpStatus.OK);
    }
}
