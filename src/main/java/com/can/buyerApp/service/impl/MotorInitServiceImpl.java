package com.can.buyerApp.service.impl;

import com.can.buyerApp.config.AppConfig;
import com.can.buyerApp.entity.*;
import com.can.buyerApp.masterentity.PaymentTag;
import com.can.buyerApp.masterentity.PaymentTagDetail;
import com.can.buyerApp.repository.*;
import com.can.buyerApp.request.InitRequest;
import com.can.buyerApp.request.SecondInitRequest;
import com.can.buyerApp.service.MotorInitService;
import com.can.buyerApp.utils.DateTimeUtils;
import com.can.buyerApp.webclient.OndcWebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.can.buyerApp.constants.PreConstants.CITY_CODE;
import static com.can.buyerApp.constants.PreConstants.INIT;

@Slf4j
@Service
public class MotorInitServiceImpl implements MotorInitService {

    private final OndcWebClient ondcWebClient;
    private final ContextRepository contextRepository;
    private final FormStatusRepository formStatusRepository;
    private final MotorOnSelectRepository motorOnSelectRepository;
    private final MotorVehicleFormRepository vehicleDetailsRepository;
    private final PaymentTagRepo paymentTagRepo;
    private final AppConfig appConfig;


    @Value("${api.bap.url}")
    private String apiBapUrl;

    public MotorInitServiceImpl(OndcWebClient ondcWebClient, ContextRepository contextRepository, FormStatusRepository formStatusRepository, MotorOnSelectRepository motorOnSelectRepository, MotorVehicleFormRepository vehicleDetailsRepository, PaymentTagRepo paymentTagRepo, AppConfig appConfig) {
        this.ondcWebClient = ondcWebClient;
        this.contextRepository = contextRepository;
        this.formStatusRepository = formStatusRepository;
        this.motorOnSelectRepository = motorOnSelectRepository;
        this.vehicleDetailsRepository = vehicleDetailsRepository;
        this.paymentTagRepo = paymentTagRepo;
        this.appConfig = appConfig;
    }

    @Override
    public ResponseEntity<?> sendInitRequest(String domain, String transactionId, String submissionId, String formStatus, String messageId) {

        Optional<FormStatus> kycStatusOpt = formStatusRepository.findByTransactionIdAndSubmissionId(transactionId, submissionId);
        // ================= FIRST INIT =================
        if (kycStatusOpt.isPresent()) {
            FormStatus kycStatus = kycStatusOpt.get();

            if ("APPROVED".equalsIgnoreCase(kycStatus.getStatus())) {
                InitRequest firstInitRequest =
                        createFirstInitRequest(
                                domain,
                                transactionId,
                                submissionId,
                                messageId, formStatus
                        );

                ResponseEntity<?> response = ondcWebClient.sendFirstInitRequest(firstInitRequest);
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.badRequest().body(
                    "KYC form is not APPROVED. Current status: "
                            + kycStatus.getStatus()
            );
        }
        // ================= SECOND INIT =================Third Init
        SecondInitRequest secondInitRequest =
                createSecondInitRequest(domain, transactionId, submissionId, formStatus, messageId
                );

        ResponseEntity<?> response =
                ondcWebClient.sendSecondInitRequest(secondInitRequest);

        return ResponseEntity.ok(response);
    }


    public InitRequest createFirstInitRequest(String domain, String transactionId, String submissionId, String msgId, String formStatus) {

        MotorOnSelectEntity motorOnSelectEntity =
                motorOnSelectRepository
                        .findByTransactionIdAndMessageId(transactionId, msgId);

        ContextEntity contextDetails =
                contextRepository.findByTransactionAndIsSelected(transactionId);

        MotorVehicleForm vehicleDetails = vehicleDetailsRepository.findByTransactionId(transactionId);

        InitRequest request = new InitRequest();

        // ================= CONTEXT =================
        InitRequest.Context context = new InitRequest.Context();
        context.setAction(INIT);
        context.setBap_id(contextDetails.getBap_id());
        context.setBap_uri(apiBapUrl);
        context.setBpp_id(contextDetails.getBpp_id());
        context.setBpp_uri(contextDetails.getBpp_uri());
        context.setDomain(domain);
        context.setMessage_id(UUID.randomUUID().toString());
        context.setTransaction_id(transactionId);
        context.setTimestamp(DateTimeUtils.getCurrentFormattedTimestamp());
        context.setTtl(contextDetails.getTtl());
        context.setVersion(contextDetails.getVersion());

        InitRequest.Context.Location location = new InitRequest.Context.Location();
        InitRequest.Context.Location.Country country =
                new InitRequest.Context.Location.Country();
        country.setCode(contextDetails.getLocation_country_code());
        location.setCountry(country);

        InitRequest.Context.Location.City city =
                new InitRequest.Context.Location.City();
        city.setCode(CITY_CODE);
        location.setCity(city);

        context.setLocation(location);
        request.setContext(context);

        // ================= MESSAGE =================
        InitRequest.Message message = new InitRequest.Message();
        InitRequest.Message.Order order = new InitRequest.Message.Order();

        // ================= FULFILLMENTS =================
        InitRequest.Message.Order.Fulfillment fulfillment =
                new InitRequest.Message.Order.Fulfillment();

        InitRequest.Message.Order.Fulfillment.Customer customer =
                new InitRequest.Message.Order.Fulfillment.Customer();

        InitRequest.Message.Order.Fulfillment.Customer.Contact contact =
                new InitRequest.Message.Order.Fulfillment.Customer.Contact();
        contact.setEmail(vehicleDetails.getEmail());
        contact.setPhone(vehicleDetails.getPhone());

        InitRequest.Message.Order.Fulfillment.Customer.Person person =
                new InitRequest.Message.Order.Fulfillment.Customer.Person();
        person.setName(vehicleDetails.getFirstName());

        customer.setContact(contact);
        customer.setPerson(person);
        fulfillment.setCustomer(customer);

        order.setFulfillments(List.of(fulfillment));

        // ================= ITEM =================
        InitRequest.Message.Order.Item item =
                new InitRequest.Message.Order.Item();
        item.setId(motorOnSelectEntity.getItemId());
        item.setParent_item_id(motorOnSelectEntity.getParentItemId());

        // ================= ADD ONS =================
        List<InitRequest.Message.Order.Item.AddOn> addOns = new ArrayList<>();

        for (Map<String, Object> addon : motorOnSelectEntity.getAddOnsMap()) {

            InitRequest.Message.Order.Item.AddOn addOn =
                    new InitRequest.Message.Order.Item.AddOn();
            addOn.setId(addon.get("addonId").toString());

            InitRequest.Message.Order.Item.AddOn.Quantity.Selected selected =
                    new InitRequest.Message.Order.Item.AddOn.Quantity.Selected();
            selected.setCount(
                    Integer.parseInt(addon.get("selectedCount").toString())
            );

            InitRequest.Message.Order.Item.AddOn.Quantity quantity =
                    new InitRequest.Message.Order.Item.AddOn.Quantity();
            quantity.setSelected(selected);

            addOn.setQuantity(quantity);
            addOns.add(addOn);
        }

        item.setAdd_ons(addOns);

        // ================= XINPUT =================
        InitRequest.Message.Order.Item.XInput xinput =
                new InitRequest.Message.Order.Item.XInput();

        InitRequest.Message.Order.Item.XInput.Form form =
                new InitRequest.Message.Order.Item.XInput.Form();
        form.setId(motorOnSelectEntity.getFormId());
        xinput.setForm(form);

        InitRequest.Message.Order.Item.XInput.FormResponse formResponse =
                new InitRequest.Message.Order.Item.XInput.FormResponse();
        formResponse.setStatus(formStatus);
        formResponse.setSubmission_id(submissionId);

        xinput.setForm_response(formResponse);
        item.setXinput(xinput);

        order.setItems(List.of(item));

        // ================= PROVIDER =================
        InitRequest.Message.Order.Provider provider =
                new InitRequest.Message.Order.Provider();
        provider.setId(contextDetails.getProviderId());
        order.setProvider(provider);

        // ================= PAYMENT =================
        InitRequest.Message.Order.Payment payment =
                new InitRequest.Message.Order.Payment();
        payment.setCollected_by(appConfig.getCollectedBy());
        payment.setStatus(appConfig.getPaymentStatus());
        payment.setType(appConfig.getPaymentType());

        InitRequest.Message.Order.Payment.Params params =
                new InitRequest.Message.Order.Payment.Params();
        params.setAmount(motorOnSelectEntity.getTotalPrice());
        params.setBank_account_number(appConfig.getBankAccountNumber());
        params.setBank_code(appConfig.getBankCode());
        params.setCurrency(appConfig.getCurrency());

        payment.setParams(params);

        // ================= PAYMENT TAGS (INLINE) =================
        List<InitRequest.Message.Order.Payment.Tag> tags = new ArrayList<>();

        List<PaymentTag> paymentTags = paymentTagRepo.findAllWithDetails();

        for (PaymentTag dbTag : paymentTags) {

            InitRequest.Message.Order.Payment.Tag tag =
                    new InitRequest.Message.Order.Payment.Tag();

            InitRequest.Message.Order.Payment.Tag.Descriptor tagDescriptor =
                    new InitRequest.Message.Order.Payment.Tag.Descriptor();
            tagDescriptor.setCode(dbTag.getDescriptorType());

            tag.setDescriptor(tagDescriptor);
            tag.setDisplay(dbTag.isRequired());

            List<InitRequest.Message.Order.Payment.Tag.Value> values =
                    new ArrayList<>();

            for (PaymentTagDetail dbDetail : dbTag.getDetails()) {

                InitRequest.Message.Order.Payment.Tag.Value value =
                        new InitRequest.Message.Order.Payment.Tag.Value();

                InitRequest.Message.Order.Payment.Tag.Value.Descriptor valueDescriptor =
                        new InitRequest.Message.Order.Payment.Tag.Value.Descriptor();
                valueDescriptor.setCode(dbDetail.getDescriptorType());

                value.setDescriptor(valueDescriptor);
                value.setValue(dbDetail.getValue());

                values.add(value);
            }

            tag.setList(values);
            tags.add(tag);
        }

        payment.setTags(tags);

        order.setPayments(List.of(payment));

        message.setOrder(order);
        request.setMessage(message);

        return request;
    }


    public SecondInitRequest createSecondInitRequest(
            String domain,
            String transactionId,
            String submissionId,
            String formStatus,
            String messageId
    ) {

        MotorOnSelectEntity motorOnSelect =
                motorOnSelectRepository
                        .findByTransactionIdAndMessageId(transactionId, messageId);

        ContextEntity contextDetails =
                contextRepository.findByTransactionAndIsSelected(transactionId);

        SecondInitRequest request = new SecondInitRequest();

        // ================= CONTEXT =================
        SecondInitRequest.Context context = new SecondInitRequest.Context();
        context.setAction(INIT);
        context.setBap_id(contextDetails.getBap_id());
        context.setBap_uri(apiBapUrl);
        context.setBpp_id(contextDetails.getBpp_id());
        context.setBpp_uri(contextDetails.getBpp_uri());
        context.setDomain(domain);
        context.setMessage_id(UUID.randomUUID().toString());
        context.setTransaction_id(transactionId);
        context.setTimestamp(DateTimeUtils.getCurrentFormattedTimestamp());
        context.setTtl(contextDetails.getTtl());
        context.setVersion(contextDetails.getVersion());

        SecondInitRequest.Context.Location location =
                new SecondInitRequest.Context.Location();

        SecondInitRequest.Context.Location.Country country =
                new SecondInitRequest.Context.Location.Country();
        country.setCode(contextDetails.getLocation_country_code());

        SecondInitRequest.Context.Location.City city =
                new SecondInitRequest.Context.Location.City();
        city.setCode(CITY_CODE);

        location.setCountry(country);
        location.setCity(city);
        context.setLocation(location);

        request.setContext(context);

        // ================= MESSAGE =================
        SecondInitRequest.Message message = new SecondInitRequest.Message();
        SecondInitRequest.Message.Order order =
                new SecondInitRequest.Message.Order();

        // ================= FULFILLMENT =================
        SecondInitRequest.Message.Order.Fulfillment fulfillment =
                new SecondInitRequest.Message.Order.Fulfillment();

        fulfillment.setId(motorOnSelect.getFulfillmentId());

        SecondInitRequest.Message.Order.Fulfillment.Customer customer =
                new SecondInitRequest.Message.Order.Fulfillment.Customer();
        Map<String, Object> customerMap =
                motorOnSelect.getCustomerDetailsMap();

        if (customerMap != null) {

            SecondInitRequest.Message.Order.Fulfillment.Customer.Contact contact =
                    new SecondInitRequest.Message.Order.Fulfillment.Customer.Contact();

            contact.setEmail(
                    customerMap.get("email") != null
                            ? customerMap.get("email").toString()
                            : null
            );

            contact.setPhone(
                    customerMap.get("phone") != null
                            ? customerMap.get("phone").toString()
                            : null
            );

            SecondInitRequest.Message.Order.Fulfillment.Customer.Person person =
                    new SecondInitRequest.Message.Order.Fulfillment.Customer.Person();

            person.setName(
                    customerMap.get("name") != null
                            ? customerMap.get("name").toString()
                            : null
            );

            customer.setContact(contact);
            customer.setPerson(person);
        }


        fulfillment.setCustomer(customer);
        order.setFulfillments(List.of(fulfillment));

        // ================= ITEM =================
        SecondInitRequest.Message.Order.Item item =
                new SecondInitRequest.Message.Order.Item();

        item.setId(motorOnSelect.getItemId());
        item.setParent_item_id(motorOnSelect.getParentItemId());

        // ================= ADD ONS =================
        List<SecondInitRequest.Message.Order.Item.AddOn> addOns =
                new ArrayList<>();

        List<Map<String, Object>> addOnList =
                motorOnSelect.getAddOnsMap();

        if (addOnList != null) {
            for (Map<String, Object> addon : addOnList) {

                SecondInitRequest.Message.Order.Item.AddOn addOn =
                        new SecondInitRequest.Message.Order.Item.AddOn();

                addOn.setId(addon.get("addonId").toString());

                SecondInitRequest.Message.Order.Item.AddOn.Quantity.Selected selected =
                        new SecondInitRequest.Message.Order.Item.AddOn.Quantity.Selected();
                selected.setCount(
                        Integer.parseInt(addon.get("selectedCount").toString())
                );

                SecondInitRequest.Message.Order.Item.AddOn.Quantity quantity =
                        new SecondInitRequest.Message.Order.Item.AddOn.Quantity();
                quantity.setSelected(selected);

                addOn.setQuantity(quantity);
                addOns.add(addOn);
            }
        }

        item.setAdd_ons(addOns);

        // ================= XINPUT =================
        SecondInitRequest.Message.Order.Item.XInput xinput =
                new SecondInitRequest.Message.Order.Item.XInput();

        SecondInitRequest.Message.Order.Item.XInput.Form form =
                new SecondInitRequest.Message.Order.Item.XInput.Form();
        form.setId(motorOnSelect.getFormId());

        SecondInitRequest.Message.Order.Item.XInput.FormResponse formResponse =
                new SecondInitRequest.Message.Order.Item.XInput.FormResponse();
        formResponse.setStatus(formStatus);
        formResponse.setSubmission_id(submissionId);

        xinput.setForm(form);
        xinput.setForm_response(formResponse);
        item.setXinput(xinput);

        order.setItems(List.of(item));

        // ================= PROVIDER =================
        SecondInitRequest.Message.Order.Provider provider =
                new SecondInitRequest.Message.Order.Provider();
        provider.setId(contextDetails.getProviderId());
        order.setProvider(provider);

        // ================= PAYMENT =================
        SecondInitRequest.Message.Order.Payment payment =
                new SecondInitRequest.Message.Order.Payment();

        payment.setCollected_by(appConfig.getCollectedBy());
        payment.setStatus(appConfig.getPaymentStatus());
        payment.setType(appConfig.getPaymentType());

        SecondInitRequest.Message.Order.Payment.Params params =
                new SecondInitRequest.Message.Order.Payment.Params();
        params.setAmount(motorOnSelect.getTotalPrice());
        params.setBank_account_number(appConfig.getBankAccountNumber());
        params.setBank_code(appConfig.getBankCode());
        params.setCurrency(appConfig.getCurrency());

        payment.setParams(params);

        // ================= PAYMENT TAGS =================
        List<SecondInitRequest.Message.Order.Payment.Tag> tags =
                new ArrayList<>();

        List<PaymentTag> paymentTags =
                paymentTagRepo.findAllWithDetails();

        for (PaymentTag dbTag : paymentTags) {

            SecondInitRequest.Message.Order.Payment.Tag tag =
                    new SecondInitRequest.Message.Order.Payment.Tag();

            SecondInitRequest.Message.Order.Payment.Tag.Descriptor descriptor =
                    new SecondInitRequest.Message.Order.Payment.Tag.Descriptor();
            descriptor.setCode(dbTag.getDescriptorType());

            tag.setDescriptor(descriptor);
            tag.setDisplay(dbTag.isRequired());

            List<SecondInitRequest.Message.Order.Payment.Tag.Value> values =
                    new ArrayList<>();

            for (PaymentTagDetail dbDetail : dbTag.getDetails()) {

                SecondInitRequest.Message.Order.Payment.Tag.Value value =
                        new SecondInitRequest.Message.Order.Payment.Tag.Value();

                SecondInitRequest.Message.Order.Payment.Tag.Value.Descriptor valueDescriptor =
                        new SecondInitRequest.Message.Order.Payment.Tag.Value.Descriptor();
                valueDescriptor.setCode(dbDetail.getDescriptorType());

                value.setDescriptor(valueDescriptor);
                value.setValue(dbDetail.getValue());

                values.add(value);
            }

            tag.setList(values);
            tags.add(tag);
        }

        payment.setTags(tags);
        order.setPayments(List.of(payment));

        message.setOrder(order);
        request.setMessage(message);

        return request;
    }


}
