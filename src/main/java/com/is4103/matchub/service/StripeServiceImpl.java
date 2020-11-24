package com.is4103.matchub.service;

import com.is4103.matchub.entity.DonationEntity;
import com.is4103.matchub.enumeration.PaymentScenario;
import com.is4103.matchub.exception.DonationOptionNotFoundException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.exception.ResourceNotFoundException;
import com.is4103.matchub.exception.StripeRuntimeException;
import com.is4103.matchub.vo.PaymentIntentCreateVO;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.Balance;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.LoginLink;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author tjle2
 */
@Service
public class StripeServiceImpl implements StripeService {

    @Autowired
    UserService userService;
    
    @Autowired
    FundCampaignService fundCampaignService;
    
    @Autowired
    ResourceRequestService resourceRequestService;

    @Value("${stripe.sk}")
    private String apiKey;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;
    
    

    @PostConstruct
    public void initialize() {

        Stripe.apiKey = apiKey;
    }

    @Override
    public AccountLink createStripeAccount(String email) {

        AccountCreateParams params = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.EXPRESS)
                .setCapabilities( // setting such that they will enable transfers and payments immediately
                        AccountCreateParams.Capabilities.builder()
                                .setCardPayments(
                                        AccountCreateParams.Capabilities.CardPayments.builder()
                                                .setRequested(true)
                                                .build())
                                .setTransfers(
                                        AccountCreateParams.Capabilities.Transfers.builder()
                                                .setRequested(true)
                                                .build())
                                .build())
                .build();

        try {
            Account account = Account.create(params);

            // store stripe account uid first, still dependent on whether onboarding is successful.
            // another boolean to update later when charges are enabled in the account.
            userService.setUserStripeAccountUid(email, account.getId());

            // creating account and link for user to setup, FE will push to the generated link for them to setup.
            AccountLinkCreateParams createAccountLinkParams = AccountLinkCreateParams.builder()
                    .setAccount(account.getId())
                    .setRefreshUrl("http://localhost:3000/returnFromStripe")
                    .setReturnUrl("http://localhost:3000/returnFromStripe")
                    .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                    .build();

            return AccountLink.create(createAccountLinkParams);

        } catch (StripeException ex) {
            throw new StripeRuntimeException(ex.getMessage());
        }
    }

    @Override
    public PaymentIntent createPaymentIntent(PaymentIntentCreateVO paymentIntentCreateVO) {
    
        if(paymentIntentCreateVO.getPaymentScenario()==PaymentScenario.FundCampaignDonation){
          return createPaymentIntentForFundCampaignDonation(paymentIntentCreateVO);
        }else if(paymentIntentCreateVO.getPaymentScenario()==PaymentScenario.ResourcePurchase){
          return createPaymentIntentForResourcePurchase(paymentIntentCreateVO);
        }
       return new PaymentIntent();
    }
    
    public PaymentIntent createPaymentIntentForFundCampaignDonation(PaymentIntentCreateVO paymentIntentCreateVO){
         PaymentIntentCreateParams params
                = PaymentIntentCreateParams.builder()
                        .setAmount(paymentIntentCreateVO.getAmountInCents())
                        .setCurrency("sgd")
                        .addPaymentMethodType("card")
                        // for commission charges
                        .setApplicationFeeAmount(calculatePlatformCommissionFees(paymentIntentCreateVO.getAmountInCents()))
                        // for setting who to transfer to
                        .setTransferData(
                                PaymentIntentCreateParams.TransferData.builder()
                                        .setDestination(paymentIntentCreateVO.getPayeeStripeUid())
                                        .build())
                        // setting metadata for payment success method to use later, will have payer uid
                        // resource_id (if purchasing resources) and donation_option_id (crowdfunding).
                        // can add on if other fields are needed.
                        .putMetadata("payer_stripe_uid", paymentIntentCreateVO.getPayerStripeUid())
                        .putMetadata("donation_option_id", paymentIntentCreateVO.getDonationOptionId().toString())
                        .putMetadata("scenario", "FundCampaignDonation")
                        .build();

        try {
            return PaymentIntent.create(params);
        } catch (StripeException ex) {
            throw new StripeRuntimeException(ex.getMessage());
        }
        
    }
    
    
    public PaymentIntent createPaymentIntentForResourcePurchase(PaymentIntentCreateVO paymentIntentCreateVO){
       PaymentIntentCreateParams params
                = PaymentIntentCreateParams.builder()
                        .setAmount(paymentIntentCreateVO.getAmountInCents())
                        .setCurrency("sgd")
                        .addPaymentMethodType("card")
                        // for commission charges
                        .setApplicationFeeAmount(calculatePlatformCommissionFees(paymentIntentCreateVO.getAmountInCents()))
                        // for setting who to transfer to
                        .setTransferData(
                                PaymentIntentCreateParams.TransferData.builder()
                                        .setDestination(paymentIntentCreateVO.getPayeeStripeUid())
                                        .build())
                        // setting metadata for payment success method to use later, will have payer uid
                        // resource_id (if purchasing resources) and donation_option_id (crowdfunding).
                        // can add on if other fields are needed.
                        .putMetadata("payer_stripe_uid", paymentIntentCreateVO.getPayerStripeUid())
                        .putMetadata("resource_id", paymentIntentCreateVO.getResourceId().toString())
                        .putMetadata("project_id", paymentIntentCreateVO.getResourceId().toString())
                        .putMetadata("scenario", "ResourcePurchase")
                        .build();

        try {
            return PaymentIntent.create(params);
        } catch (StripeException ex) {
            throw new StripeRuntimeException(ex.getMessage());
        }
    }

    private Long calculatePlatformCommissionFees(Long amountInCents) {
        // 10% commission to our platform, can always change
        return (long) (amountInCents * 0.1);
    }

    @Override
    public String handleWebhookEvent(String json, HttpServletRequest request) throws DonationOptionNotFoundException, ResourceNotFoundException, ProjectNotFoundException{

        String header = request.getHeader("Stripe-Signature");
        // need to generate from stripe CLI and change in application.properties file
        String endpointSecret = stripeWebhookSecret;

        Event event = null;

        try {
            event = Webhook.constructEvent(json, header, endpointSecret);
        } catch (SignatureVerificationException e) {
            e.printStackTrace();
        }

        // for checking user setup flow, and whether they went through until account charges are enabled.
        if ("account.updated".equals(event.getType())) {
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            Account account = null;
            if (dataObjectDeserializer.getObject().isPresent()) {
                account = (Account) dataObjectDeserializer.getObject().get();
                // if user finished setup of stripe correctly, set the charges enabled to true 
                // so they are deemed to onboard onto stripe correctly.
                if (account.getChargesEnabled()) {
                    userService.setUserStripeAccountChargesEnabled(account.getId());
                }
            } else {
                // Deserialization failed, probably due to an API version mismatch.
                // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
                // instructions on how to handle this case, or return an error here.
            }
        }

        // marks payment success
        if ("payment_intent.succeeded".equals(event.getType())) {
            // Deserialize the nested object inside the event
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            PaymentIntent paymentIntent = null;

            if (dataObjectDeserializer.getObject().isPresent()) {
                paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();

                // may not always have, depends on whether FE got post this when checking out.
                String payerEmail = paymentIntent.getReceiptEmail();

                // associations in another method.
                handleSuccessfulPaymentIntent(payerEmail, paymentIntent);
            } else {
                // Deserialization failed, probably due to an API version mismatch.
                // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
                // instructions on how to handle this case, or return an error here.
            }
        }

        return "";
    }

    // to create entities and associate with fund pledge / transaction history / send out notifications with FCM etc.
    private void handleSuccessfulPaymentIntent(String payerEmail, PaymentIntent paymentIntent)throws DonationOptionNotFoundException, ResourceNotFoundException, ProjectNotFoundException{

        // if the payment if for fund campaign, create donation entity
        if(paymentIntent.getMetadata().get("scenario").equals("FundCampaignDonation")){
          fundCampaignService.createDonation(payerEmail, paymentIntent);
        }else if(paymentIntent.getMetadata().get("scenario").equals("ResourcePurchase")){
           resourceRequestService.createResourceTransaction(payerEmail, paymentIntent);
        }
        
        // to do
    }

    // generates link for user to view lightweight stripe dashboard at their url
    @Override
    public LoginLink getStripeExpressDashboard(String stripeAccountUid) {

        Map<String, Object> params = new HashMap<>();
        params.put("redirect_url", "http://localhost:3000/home");

        try {
            return LoginLink.createOnAccount(stripeAccountUid, params, null);
        } catch (StripeException ex) {
            throw new StripeRuntimeException(ex.getMessage());
        }
    }

    // get user's stripe account balance
    @Override
    public Balance getStripeBalanceByStripeAccount(String stripeAccountUid) {
        RequestOptions requestOptions = RequestOptions.builder().
                setStripeAccount(stripeAccountUid).build();

        try {
            return Balance.retrieve(requestOptions);
        } catch (StripeException ex) {
            throw new StripeRuntimeException(ex.getMessage());
        }
    }
}
