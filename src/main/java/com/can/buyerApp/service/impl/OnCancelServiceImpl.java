package com.can.buyerApp.service.impl;

import com.can.buyerApp.dto.Acknowledgement;
import com.can.buyerApp.entity.CancelDetails;
import com.can.buyerApp.mapper.RequestMapper;
import com.can.buyerApp.masterentity.CancelReason;
import com.can.buyerApp.repository.CancelReasonRepo;
import com.can.buyerApp.request.OnCancelRequest;
import com.can.buyerApp.repository.CancelRepository;
import com.can.buyerApp.service.OnCancelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OnCancelServiceImpl implements OnCancelService {

    private final CancelRepository cancelRepository;
    private final RequestMapper requestMapper;
    private final CancelReasonRepo cancelReasonRepo;

    public OnCancelServiceImpl(CancelRepository cancelRepository,RequestMapper requestMapper, CancelReasonRepo cancelReasonRepo) {
        this.cancelRepository = cancelRepository;
        this.requestMapper=requestMapper;
        this.cancelReasonRepo=cancelReasonRepo;
    }

    @Override
    public ResponseEntity<?> saveOnCancelRequest(OnCancelRequest onCancelRequest) {
        log.info("Received On cancel Request  :{}", onCancelRequest);

            if (Objects.isNull(onCancelRequest) || Objects.isNull(onCancelRequest.getMessage())) {
                log.error("On CancelRequest is null. Unable to proceed.");
               throw new RuntimeException("Invalid request: onCancelRequest or Message cannot be null.");
            }

            OnCancelRequest.Message.Order order = onCancelRequest.getMessage().getOrder();
            if (Objects.isNull(order)) {
                log.error("Invalid request: Order cannot be null.");
                throw new RuntimeException("Invalid request: Order cannot be null.");
            }
            OnCancelRequest.Message.Order.Quote quote = order.getQuote();
            if (Objects.isNull(quote)) {
                throw new RuntimeException("Invalid request: Quote cannot be null.");
            }
        try {

            List<CancelDetails> cancelDetails = order.getItems().stream()
                    .filter(Objects::nonNull)
                    .map(item -> {
                        CancelDetails entity = new CancelDetails();
                        entity.setItemId(item.getId());
                        entity.setPolicyId(onCancelRequest.getMessage().getOrder().getId());
                        entity.setTransaction_id(onCancelRequest.getContext().getTransaction_id());
                        entity.setMessageId(onCancelRequest.getContext().getMessage_id());
                        entity.setQuoteId(quote.getId());
                        entity.setStatus(order.getStatus());
                        entity.setUpdatedAt(LocalDateTime.now());
                        return entity;
                    })
                    .collect(Collectors.toList());

            cancelRepository.saveAll(cancelDetails);
            log.info("Saved Cancel details: {}", cancelDetails);
            Acknowledgement acknowledgement = requestMapper.cancelAckResponse(onCancelRequest);
            return ResponseEntity.ok(acknowledgement);
        }
        catch (Exception ex) {
                log.error("Error saving OnSelect details: {}", ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving OnSelect details.");
            }
        }

    @Override
    public List<CancelReason> getCancelReason() {
        return cancelReasonRepo.findAll();
    }

}
