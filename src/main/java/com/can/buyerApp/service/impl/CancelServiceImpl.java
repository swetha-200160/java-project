package com.can.buyerApp.service.impl;

import com.can.buyerApp.constants.PreConstants;
import com.can.buyerApp.entity.ContextEntity;
import com.can.buyerApp.masterentity.CancelReason;
import com.can.buyerApp.repository.CancelReasonRepo;
import com.can.buyerApp.repository.ContextRepository;
import com.can.buyerApp.request.CancelRequest;
import com.can.buyerApp.service.CancelService;
import com.can.buyerApp.utils.DateTimeUtils;
import com.can.buyerApp.webclient.OndcWebClient;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.can.buyerApp.constants.PreConstants.CANCEL;
import static com.can.buyerApp.constants.PreConstants.CITY_CODE;

@Slf4j
@Service
public class CancelServiceImpl implements CancelService {

    private final ContextRepository contextRepository;
    private final OndcWebClient ondcWebClient;
    private final CancelReasonRepo cancelReasonRepo;

    public CancelServiceImpl(
            ContextRepository contextRepository,
            OndcWebClient ondcWebClient,
            CancelReasonRepo cancelReasonRepo
    ) {
        this.contextRepository = contextRepository;
        this.ondcWebClient = ondcWebClient;
        this.cancelReasonRepo = cancelReasonRepo;
    }

    @Override
    public ResponseEntity<?> sendCancelRequest(
            String domain,
            String type,
            String transactionId,
            Long cancellationReasonId,
            String orderId,
            String description
    ) {

        try {
            CancelRequest cancelRequest =
                    createCancelRequest(domain, transactionId, cancellationReasonId, orderId, description);

            ResponseEntity<?> response = ondcWebClient.sendCancel(cancelRequest);
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            log.error("Error in processing Cancel request", e);
            throw new RuntimeException("Error in processing Cancel request", e);
        }
    }

    private CancelRequest createCancelRequest(
            String domain,
            String transactionId,
            Long cancellationReasonId,
            String orderId,
            String description
    ) {

        CancelReason reason =
                cancelReasonRepo.findAllById(cancellationReasonId);

        ContextEntity contextDetail =
                contextRepository.findByTransactionAndIsSelected(transactionId);

        CancelRequest request = new CancelRequest();

        String messageId = UUID.randomUUID().toString();

        // -------- Context --------
        CancelRequest.Context context = new CancelRequest.Context();
        context.setAction(CANCEL);
        context.setDomain(domain);
        context.setTransaction_id(transactionId);
        context.setMessage_id(messageId);
        context.setTimestamp(DateTimeUtils.getCurrentFormattedTimestamp());
        context.setTtl(contextDetail.getTtl());
        context.setVersion(contextDetail.getVersion());
        context.setBap_id(contextDetail.getBap_id());
        context.setBap_uri(contextDetail.getBap_uri());
        context.setBpp_id(contextDetail.getBpp_id());
        context.setBpp_uri(contextDetail.getBpp_uri());

        // -------- Location --------
        CancelRequest.Context.Location location =
                new CancelRequest.Context.Location();

        CancelRequest.Context.Location.City city =
                new CancelRequest.Context.Location.City();
        city.setCode(CITY_CODE);

        CancelRequest.Context.Location.Country country =
                new CancelRequest.Context.Location.Country();
        country.setCode(contextDetail.getLocation_country_code());

        location.setCity(city);
        location.setCountry(country);

        context.setLocation(location);
        request.setContext(context);

        // -------- Message --------
        CancelRequest.Message message = new CancelRequest.Message();
        message.setOrder_id(orderId);
        message.setCancellation_reason_id(reason.getId().toString());

        CancelRequest.Message.Descriptor descriptor =
                new CancelRequest.Message.Descriptor();
        descriptor.setShort_desc(description);

        message.setDescriptor(descriptor);
        request.setMessage(message);

        return request;
    }
}
