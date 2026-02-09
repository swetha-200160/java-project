package com.can.buyerApp.service.impl;

import com.can.buyerApp.config.AppConfig;
import com.can.buyerApp.entity.*;
import com.can.buyerApp.repository.*;
import com.can.buyerApp.masterentity.PaymentTag;
import com.can.buyerApp.masterentity.PaymentTagDetail;
import com.can.buyerApp.request.MotorConfirmRequest;
import com.can.buyerApp.service.ConfirmService;
import com.can.buyerApp.utils.DateTimeUtils;
import com.can.buyerApp.webclient.OndcWebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.util.*;

import static com.can.buyerApp.constants.PreConstants.*;

@Slf4j
@Service
public class ConfirmServiceImpl implements ConfirmService {

    private final OndcWebClient ondcWebClient;
    private final ContextRepository contextRepository;
    private final MotorOnSelectRepository motorOnSelectRepository;
    private final PaymentTagRepo paymentTagRepo;
    private final NomineeDetailsRepository nomineeDetailsRepository;
    private final AppConfig appConfig;
    private final IndividualFormRepository individualFormRepository;
    private final FamilyFormRepository familyFormRepository;
    private final MotorPaymentRepository motorPaymentRepository;

    public ConfirmServiceImpl(OndcWebClient ondcWebClient, ContextRepository contextRepository, MotorOnSelectRepository onSelectRepository, PaymentTagRepo paymentTagRepo, NomineeDetailsRepository nomineeDetailsRepository, AppConfig appConfig, IndividualFormRepository individualFormRepository, FamilyFormRepository familyFormRepository, MotorPaymentRepository motorPaymentRepository) {
        this.ondcWebClient = ondcWebClient;
        this.contextRepository = contextRepository;
        this.motorOnSelectRepository = onSelectRepository;
        this.paymentTagRepo=paymentTagRepo;
        this.nomineeDetailsRepository=nomineeDetailsRepository;
        this.appConfig=appConfig;
        this.individualFormRepository=individualFormRepository;
        this.familyFormRepository=familyFormRepository;
        this.motorPaymentRepository = motorPaymentRepository;
    }

    @Value("${api.bap.url}")
    private String apiBapUrl;

    @Override
    public ResponseEntity<?> sendConfirmRequest(String domain, String transactionId, String submissionId,String formId, String formStatus,String messageId) {

        try{
           MotorConfirmRequest confirmRequest = confirmRequest(domain, transactionId, submissionId,formId,formStatus,messageId);
            ResponseEntity<?> response = ondcWebClient.sendConfirm(confirmRequest);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            log.error("Error in processing Confirm request", e);
            throw new RuntimeException("Error in processing Confirm request", e);
        }
    }


    @Override
    public MotorConfirmRequest confirmRequest(
            String domain,
            String transactionId,
            String submissionId,
            String formId,
            String formStatus,
            String msgId) {

        // ================= FETCH DATA =================
        ContextEntity contextDetail =
                contextRepository.findByTransactionAndIsSelected(transactionId);

        MotorOnSelectEntity motorOnSelectEntity =
                motorOnSelectRepository
                        .findByTransactionIdAndMessageId(transactionId, msgId);

        Optional<MotorPaymentDetails> motorPaymentDetails = motorPaymentRepository.findByTransactionId(transactionId);

        MotorConfirmRequest confirmRequest = new MotorConfirmRequest();

        // ================= CONTEXT =================
        MotorConfirmRequest.Context context = new MotorConfirmRequest.Context();
        context.setAction(CONFIRM);
        context.setBap_id(contextDetail.getBap_id());
        context.setBap_uri(apiBapUrl);
        context.setBpp_id(contextDetail.getBpp_id());
        context.setBpp_uri(contextDetail.getBpp_uri());
        context.setDomain(domain);
        context.setMessage_id(UUID.randomUUID().toString());
        context.setTimestamp(DateTimeUtils.getCurrentFormattedTimestamp());
        context.setTransaction_id(transactionId);
        context.setTtl(contextDetail.getTtl());
        context.setVersion(contextDetail.getVersion());

        MotorConfirmRequest.Context.Location location =
                new MotorConfirmRequest.Context.Location();

        MotorConfirmRequest.Context.Location.Country country =
                new MotorConfirmRequest.Context.Location.Country();
        country.setCode(contextDetail.getLocation_country_code());

        MotorConfirmRequest.Context.Location.City city =
                new MotorConfirmRequest.Context.Location.City();
        city.setCode(CITY_CODE);

        location.setCountry(country);
        location.setCity(city);
        context.setLocation(location);

        confirmRequest.setContext(context);

        // ================= MESSAGE =================
        MotorConfirmRequest.Message message = new MotorConfirmRequest.Message();
        MotorConfirmRequest.Message.Order order =
                new MotorConfirmRequest.Message.Order();

        // ================= PROVIDER =================
        MotorConfirmRequest.Message.Order.Provider provider =
                new MotorConfirmRequest.Message.Order.Provider();
        provider.setId(motorOnSelectEntity.getProviderId());
        order.setProvider(provider);

        // ================= FULFILLMENT =================
        MotorConfirmRequest.Message.Order.Fulfillment fulfillment =
                new MotorConfirmRequest.Message.Order.Fulfillment();

        fulfillment.setId(motorOnSelectEntity.getFulfillmentId());
        fulfillment.setType(motorOnSelectEntity.getFulfillmentType());

        Map<String, Object> customerMap =
                motorOnSelectEntity.getCustomerDetailsMap();

        MotorConfirmRequest.Message.Order.Fulfillment.Customer customer =
                new MotorConfirmRequest.Message.Order.Fulfillment.Customer();

        MotorConfirmRequest.Message.Order.Fulfillment.Customer.Contact contact =
                new MotorConfirmRequest.Message.Order.Fulfillment.Customer.Contact();
        contact.setEmail((String) customerMap.get("email"));
        contact.setPhone((String) customerMap.get("phone"));

        MotorConfirmRequest.Message.Order.Fulfillment.Customer.Person person =
                new MotorConfirmRequest.Message.Order.Fulfillment.Customer.Person();
        person.setName((String) customerMap.get("name"));

        customer.setContact(contact);
        customer.setPerson(person);
        fulfillment.setCustomer(customer);

        order.setFulfillments(List.of(fulfillment));

        // ================= ITEM =================
        MotorConfirmRequest.Message.Order.Item item =
                new MotorConfirmRequest.Message.Order.Item();

        item.setId(motorOnSelectEntity.getItemId());
        item.setParent_item_id(motorOnSelectEntity.getParentItemId());
        item.setFulfillment_ids(
                List.of(motorOnSelectEntity.getFulfillmentId())
        );

        // ================= ADD ONS =================
        List<Map<String, Object>> addOnMaps =
                motorOnSelectEntity.getAddOnsMap();

        if (addOnMaps != null) {
            List<MotorConfirmRequest.Message.Order.Item.AddOn> addOns =
                    new ArrayList<>();

            for (Map<String, Object> addOnMap : addOnMaps) {

                MotorConfirmRequest.Message.Order.Item.AddOn addOn =
                        new MotorConfirmRequest.Message.Order.Item.AddOn();
                addOn.setId(addOnMap.get("addonId").toString());

                MotorConfirmRequest.Message.Order.Item.AddOn.Quantity.Selected selected =
                        new MotorConfirmRequest.Message.Order.Item.AddOn.Quantity.Selected();
                selected.setCount(
                        Integer.parseInt(addOnMap.get("selectedCount").toString())
                );

                MotorConfirmRequest.Message.Order.Item.AddOn.Quantity quantity =
                        new MotorConfirmRequest.Message.Order.Item.AddOn.Quantity();
                quantity.setSelected(selected);

                addOn.setQuantity(quantity);
                addOns.add(addOn);
            }

            item.setAdd_ons(addOns);
        }

        // ================= XINPUT =================
        MotorConfirmRequest.Message.Order.Item.XInput xinput =
                new MotorConfirmRequest.Message.Order.Item.XInput();

        MotorConfirmRequest.Message.Order.Item.XInput.Form form =
                new MotorConfirmRequest.Message.Order.Item.XInput.Form();
        form.setId(formId);

        MotorConfirmRequest.Message.Order.Item.XInput.FormResponse formResponse =
                new MotorConfirmRequest.Message.Order.Item.XInput.FormResponse();
        formResponse.setStatus(formStatus);
        formResponse.setSubmission_id(submissionId);

        xinput.setForm(form);
        xinput.setForm_response(formResponse);
        item.setXinput(xinput);

        order.setItems(List.of(item));

//        // ================= PAYMENT =================
//        MotorConfirmRequest.Message.Order.Payment payment =
//                new MotorConfirmRequest.Message.Order.Payment();
//
//        payment.setCollected_by(appConfig.getCollectedBy());
//        payment.setStatus(appConfig.getPaymentStatus());
//        payment.setType(appConfig.getPaymentType());
//
//        MotorConfirmRequest.Message.Order.Payment.Params params =
//                new MotorConfirmRequest.Message.Order.Payment.Params();
//        params.setAmount(motorOnSelectEntity.getTotalPrice());
//        params.setBank_account_number(appConfig.getBankAccountNumber());
//        params.setBank_code(appConfig.getBankCode());
//        params.setCurrency(appConfig.getCurrency());
//        params.setTransaction_id("346356543473457");
//        payment.setParams(params);

        MotorPaymentDetails paymentEntity = motorPaymentDetails.get();

// ================= PAYMENT =================
        MotorConfirmRequest.Message.Order.Payment payment =
                new MotorConfirmRequest.Message.Order.Payment();

        payment.setCollected_by(paymentEntity.getCollectedBy());
        payment.setStatus(paymentEntity.getStatus());
        payment.setType(paymentEntity.getType());

// ================= PAYMENT PARAMS =================
        MotorConfirmRequest.Message.Order.Payment.Params params =
                new MotorConfirmRequest.Message.Order.Payment.Params();

        params.setAmount(paymentEntity.getAmount());
        params.setBank_account_number(paymentEntity.getBankAccountNumber());
        params.setBank_code(paymentEntity.getBankCode());
        params.setCurrency(paymentEntity.getCurrency());

// use messageId or DB-generated ref instead of hard-code
        params.setTransaction_id(paymentEntity.getTransactionId());

        payment.setParams(params);


        // ================= PAYMENT TAGS =================
        List<MotorConfirmRequest.Message.Order.Payment.Tag> tags =
                new ArrayList<>();

        List<PaymentTag> paymentTags =
                paymentTagRepo.findAllWithDetails();

        for (PaymentTag dbTag : paymentTags) {

            MotorConfirmRequest.Message.Order.Payment.Tag tag =
                    new MotorConfirmRequest.Message.Order.Payment.Tag();

            MotorConfirmRequest.Message.Order.Payment.Tag.Descriptor descriptor =
                    new MotorConfirmRequest.Message.Order.Payment.Tag.Descriptor();
            descriptor.setCode(dbTag.getDescriptorType());

            tag.setDescriptor(descriptor);
            tag.setDisplay(dbTag.isRequired());

            List<MotorConfirmRequest.Message.Order.Payment.Tag.Value> values =
                    new ArrayList<>();

            for (PaymentTagDetail detail : dbTag.getDetails()) {

                MotorConfirmRequest.Message.Order.Payment.Tag.Value value =
                        new MotorConfirmRequest.Message.Order.Payment.Tag.Value();

                MotorConfirmRequest.Message.Order.Payment.Tag.Value.Descriptor vd =
                        new MotorConfirmRequest.Message.Order.Payment.Tag.Value.Descriptor();
                vd.setCode(detail.getDescriptorType());

                value.setDescriptor(vd);
                value.setValue(detail.getValue());
                values.add(value);
            }

            tag.setList(values);
            tags.add(tag);
        }

        payment.setTags(tags);
        order.setPayments(List.of(payment));

        // ================= FINAL =================
        message.setOrder(order);
        confirmRequest.setMessage(message);

        return confirmRequest;
    }




}