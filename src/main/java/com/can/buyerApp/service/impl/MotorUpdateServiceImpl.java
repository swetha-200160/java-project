package com.can.buyerApp.service.impl;

import com.can.buyerApp.entity.ContextEntity;
import com.can.buyerApp.entity.MotorPolicyDocuments;
import com.can.buyerApp.repository.ContextRepository;
import com.can.buyerApp.repository.MotorDocumentRepository;
import com.can.buyerApp.request.MotorUpdateRequest;
import com.can.buyerApp.service.MotorUpdateService;
import com.can.buyerApp.utils.DateTimeUtils;
import com.can.buyerApp.webclient.OndcWebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.can.buyerApp.constants.PreConstants.*;
@Slf4j
@Service
public class MotorUpdateServiceImpl implements MotorUpdateService {

    private final OndcWebClient ondcWebClient;
    private final ContextRepository contextRepository;
    private final MotorDocumentRepository documentRepository;
    @Value("${api.bap.url}")
    private String apiBapUrl;

    public MotorUpdateServiceImpl(OndcWebClient ondcWebClient, ContextRepository contextRepository, MotorDocumentRepository documentRepository) {
        this.ondcWebClient = ondcWebClient;
        this.contextRepository = contextRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    public ResponseEntity<?> sendUpdateRequest(String domain, String transactionId, String phoneNumber) {

        try {
            log.info(
                    "Processing Update request | domain={} | txnId={} | phone={}",
                    domain, transactionId, phoneNumber
            );
            MotorUpdateRequest updateRequest = updateRequest(domain, transactionId, phoneNumber);
            ResponseEntity<?> response = ondcWebClient.sendUpdate(updateRequest);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in processing Update request", e);
            throw new RuntimeException("Error in processing Update request", e);
        }
    }


    private MotorUpdateRequest updateRequest(String domain, String transactionId, String phoneNumber) {

        ContextEntity contextDetail = contextRepository.findByTransactionAndIsSelected(transactionId);
        if (contextDetail == null) {
            throw new IllegalArgumentException("Context not found for transactionId");
        }

        // ================= CONTEXT =================
        MotorUpdateRequest updateRequest = new MotorUpdateRequest();

        MotorUpdateRequest.Context context = new MotorUpdateRequest.Context();
        context.setAction(UPDATE);
        context.setBap_id(contextDetail.getBap_id());
        context.setBap_uri(apiBapUrl);
        context.setBpp_id(contextDetail.getBpp_id());
        context.setBpp_uri(contextDetail.getBpp_uri());
        context.setDomain(domain);
        context.setMessage_id(UUID.randomUUID().toString());
        context.setTransaction_id(transactionId);
        context.setTimestamp(DateTimeUtils.getCurrentFormattedTimestamp());
        context.setTtl(contextDetail.getTtl());
        context.setVersion(contextDetail.getVersion());

        MotorUpdateRequest.Context.Location location =
                new MotorUpdateRequest.Context.Location();

        MotorUpdateRequest.Context.Location.Country country =
                new MotorUpdateRequest.Context.Location.Country();
        country.setCode(contextDetail.getLocation_country_code());

        MotorUpdateRequest.Context.Location.City city =
                new MotorUpdateRequest.Context.Location.City();
        city.setCode(CITY_CODE);

        location.setCountry(country);
        location.setCity(city);
        context.setLocation(location);

        updateRequest.setContext(context);

        // ================= MESSAGE =================
        MotorUpdateRequest.Message message = new MotorUpdateRequest.Message();
        message.setUpdate_target(ORDER_FULFILLMENTS);


        String orderId = documentRepository
                .findTopByTransactionIdOrderByCreatedAtDesc(transactionId)
                .map(MotorPolicyDocuments::getOrderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("OrderId not found for transactionId"));


        MotorUpdateRequest.Message.Order order =
                new MotorUpdateRequest.Message.Order();
        order.setId(orderId);

        MotorUpdateRequest.Message.Order.Fulfillment fulfillment =
                new MotorUpdateRequest.Message.Order.Fulfillment();

        MotorUpdateRequest.Message.Order.Fulfillment.Customer customer =
                new MotorUpdateRequest.Message.Order.Fulfillment.Customer();

        MotorUpdateRequest.Message.Order.Fulfillment.Customer.Contact contact =
                new MotorUpdateRequest.Message.Order.Fulfillment.Customer.Contact();
        contact.setPhone(phoneNumber);

        customer.setContact(contact);
        fulfillment.setCustomer(customer);

        order.setFulfillments(List.of(fulfillment));
        message.setOrder(order);

        updateRequest.setMessage(message);

        return updateRequest;
    }


}
