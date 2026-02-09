package com.can.buyerApp.service.impl;

import com.can.buyerApp.entity.ContextEntity;
import com.can.buyerApp.request.MotorStatusRequest;
import com.can.buyerApp.repository.ContextRepository;
import com.can.buyerApp.service.StatusService;
import com.can.buyerApp.utils.DateTimeUtils;
import com.can.buyerApp.webclient.OndcWebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.util.UUID;
import static com.can.buyerApp.constants.PreConstants.CITY_CODE;
import static com.can.buyerApp.constants.PreConstants.STATUS;


@Slf4j
@Service
public class StatusServiceImpl implements StatusService {

    private final OndcWebClient ondcWebClient;
    private final ContextRepository contextRepository;

    public StatusServiceImpl(OndcWebClient ondcWebClient, ContextRepository contextRepository) {
        this.ondcWebClient = ondcWebClient;
        this.contextRepository = contextRepository;

    }

    @Value("${api.bap.url}")
    private String apiBapUrl;

    @Override
    public ResponseEntity<?> sendStatusRequest(String domain, String transactionId) {

        try{
           MotorStatusRequest statusRequest = createStatusRequest(domain, transactionId);

            ResponseEntity<?> response = ondcWebClient.sendStatus(statusRequest);
            return ResponseEntity.ok(response.getBody());
        }
        catch (Exception e) {
            log.error("Error in processing Status request", e);
            throw new RuntimeException("Error in processing Status request", e);
        }
    }

    @Override
    public MotorStatusRequest createStatusRequest(String domain, String transactionId) {

        String messageId= UUID.randomUUID().toString();
        ContextEntity contextDetail = contextRepository.findByTransactionAndIsSelected(transactionId);
        MotorStatusRequest requestDTO = new MotorStatusRequest();
        // Set up the context
        MotorStatusRequest.Context context = new MotorStatusRequest.Context();
        context.setAction(STATUS);
        context.setBap_id(contextDetail.getBap_id());
        context.setBap_uri(apiBapUrl);
        context.setBpp_id(contextDetail.getBpp_id());
        context.setBpp_uri(contextDetail.getBpp_uri());
        context.setDomain(domain);
        context.setMessage_id(messageId);
        context.setTransaction_id(transactionId);
        context.setTimestamp(DateTimeUtils.getCurrentFormattedTimestamp());
        context.setTtl(contextDetail.getTtl());
        context.setVersion(contextDetail.getVersion());

        // Set location details (Country and City)
        MotorStatusRequest.Context.Location location = new MotorStatusRequest.Context.Location();
        MotorStatusRequest.Context.Location.Country country = new MotorStatusRequest.Context.Location.Country();
        country.setCode(contextDetail.getLocation_country_code());
        MotorStatusRequest.Context.Location.City city = new MotorStatusRequest.Context.Location.City();
        city.setCode(CITY_CODE);
        location.setCountry(country);
        location.setCity(city);
        context.setLocation(location);
        MotorStatusRequest.Message message = new MotorStatusRequest.Message();
        message.setRef_id(transactionId);
        requestDTO.setContext(context);
        requestDTO.setMessage(message);
        return requestDTO;
    }


}
