package com.can.buyerApp.service.impl;

import com.can.buyerApp.entity.ContextEntity;
import com.can.buyerApp.masterentity.CancelReason;
import com.can.buyerApp.repository.CancelReasonRepo;
import com.can.buyerApp.repository.ContextRepository;
import com.can.buyerApp.request.CancelRequest;
import com.can.buyerApp.service.CancelService;
import com.can.buyerApp.utils.DateTimeUtils;
import com.can.buyerApp.webclient.OndcWebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.can.buyerApp.constants.PreConstants.CANCEL;
import static com.can.buyerApp.constants.PreConstants.CITY_CODE;

@Slf4j
@Service
@ConditionalOnProperty(
        name = "feature.cancel.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class CancelServiceImpl implements CancelService {

    private final ContextRepository contextRepository;
    private final OndcWebClient ondcWebClient;
    private final CancelReasonRepo cancelReasonRepo;

    public CancelServiceImpl(ContextRepository contextRepository,
                             OndcWebClient ondcWebClient,
                             CancelReasonRepo cancelReasonRepo) {
        this.contextRepository = contextRepository;
        this.ondcWebClient = ondcWebClient;
        this.cancelReasonRepo = cancelReasonRepo;
    }

    @Value("${api.bap.url}")
    private String apiBapUrl;

    @Override
    public ResponseEntity<?> sendCancelRequest(String domain,
                                               String type,
                                               String transactionId,
                                               Long cancellationReasonId,
                                               String orderId,
                                               String description) {

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

    public CancelRequest createCancelRequest(String domain,
                                             String transactionId,
                                             Long cancellationReasonId,
                                             String orderId,
                                             String description) {

        CancelReason reasonId = cancelReasonRepo.findAllById(cancellationReasonId);
        ContextEntity contextDetail =
                contextRepository.findByTransactionAndIsSelected(transactionId);

        CancelRequest request = new CancelRequest();
        String messageId = UUID.randomUUID().toString();

        CancelRequest.Context context = new CancelRequest.Context();
        context.setAction(CANCEL);
        context.setBap_id(contextDetail.getBap_id());
        context.setBap_uri(apiBapUrl);
        context.setBpp_id(contextDetail.getBpp_id());
        context.setBpp_uri(contextDetail.getBpp_uri());
        context.setDomain(domain);
        context.setMessage_id(messageId);
        context.setTimestamp(DateTimeUtils.getCurrentFormattedTimestamp());
        context.setTransaction_id(transactionId);
        context.setTtl(contextDetail.getTtl());
        context.setVersion(contextDetail.getVersion());

        CancelRequest.Context.Location location = new CancelRequest.Context.Location();
        CancelRequest.Context.Location.Country country =
                new CancelRequest.Context.Location.Country();
        country.setCode(contextDetail.getLocation_country_code());
        location.setCountry(country);

        CancelRequest.Context.Location.City city =
                new CancelRequest.Context.Location.City();
        city.setCode(CITY_CODE);
        location.setCity(city);

        context.setLocation(location);
        request.setContext(context);

        CancelRequest.Message message = new CancelRequest.Message();
        message.setCancellation_reason_id(reasonId.getId().toString());
        message.setOrder_id(orderId);

        CancelRequest.Message.Descriptor descriptor =
                new CancelRequest.Message.Descriptor();
        descriptor.setShort_desc(description);

        message.setDescriptor(descriptor);
        request.setMessage(message);

        return request;
    }
}
