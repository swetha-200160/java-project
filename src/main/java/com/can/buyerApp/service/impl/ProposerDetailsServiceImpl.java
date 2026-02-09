package com.can.buyerApp.service.impl;

import com.can.buyerApp.dto.ProposerDetailsDTO;
import com.can.buyerApp.entity.ContextEntity;
import com.can.buyerApp.entity.ProposerDetails;
import com.can.buyerApp.exception.TransactionIdNotFoundException;
import com.can.buyerApp.repository.ContextRepository;
import com.can.buyerApp.repository.ProposerDetailsRepository;
import com.can.buyerApp.service.ProposerDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProposerDetailsServiceImpl implements ProposerDetailsService {

    private final ProposerDetailsRepository proposerDetailsRepository;
    private final RestTemplate restTemplate;
    private final ContextRepository contextRepository;

    public ProposerDetailsServiceImpl(ProposerDetailsRepository proposerDetailsRepository, RestTemplate restTemplate, ContextRepository contextRepository) {
        this.proposerDetailsRepository = proposerDetailsRepository;
        this.restTemplate = restTemplate;
        this.contextRepository = contextRepository;
    }

    @Value("${api.bap.url}")
    private String apiBapUrl;


    @Override
    public ResponseEntity<?> getProposerTransactionId(String transactionId, String messageId) {

        List<ProposerDetails> proposerDetailsList = proposerDetailsRepository.findByTransactionIdAndMessageId(transactionId, messageId);

        if (proposerDetailsList.isEmpty()) {
            throw new TransactionIdNotFoundException("Transaction ID not found");
        }

        List<ProposerDetailsDTO> proposerDTOList = new ArrayList<>();

        for (ProposerDetails proposerDetails : proposerDetailsList) {
            ProposerDetailsDTO proposer = new ProposerDetailsDTO();
            proposer.setId(proposerDetails.getId());
            proposer.setTransactionId(proposerDetails.getTransactionId());
            proposer.setEmail(proposerDetails.getEmail());
            proposer.setPhone(proposerDetails.getPhone());
            proposer.setFormId(proposerDetails.getFormId());

            String txId = proposerDetails.getTransactionId();
            String proposerForm = proposerDetails.getFormUrl();
//            String url = apiBapUrl+"proposer-url?transactionId=" + txId + "&formUrl=" + proposerForm;
            String url = "https://ondcpreprod.canvendor.co.in/"+"proposer-url?transactionId=" + txId + "&formUrl=" + proposerForm;

            proposer.setFormUrl(url);
            proposer.setMessageId(proposerDetails.getMessageId());
            proposer.setFulfillmentId(proposerDetails.getFulfillmentId());
            proposer.setFulfillmentType(proposerDetails.getFulfillmentType());
            proposerDTOList.add(proposer);
        }

        log.info("Fetching Proposer Form for the transactionId: {}", transactionId);
        return ResponseEntity.ok(proposerDTOList);
    }

    @Override
    public Map<String, String> getForm(String transactionId, String formUrl) {

        Map<String, String> data = new LinkedHashMap<>();
        ResponseEntity<String> response = restTemplate.getForEntity(formUrl, String.class);
        data.put("HTML", response.getBody());

        String html = response.getBody();
        Document doc = Jsoup.parse(html);
        Element form = doc.selectFirst("form");

        if (form != null) {

            String actionUrl = form.attr("action");
            ContextEntity providerDetails = contextRepository.findByTransactionAndIsSelected(transactionId);
            String bppUrl = providerDetails.getBpp_uri();

            if (!actionUrl.startsWith("http")) {

                if (!bppUrl.endsWith("/") && !actionUrl.startsWith("/")) {
                    data.put("Submit-Url", bppUrl + "/" + actionUrl);
                    return data;
                } else if (bppUrl.endsWith("/") && actionUrl.startsWith("/")) {
                    data.put("Submit-Url", bppUrl + actionUrl.substring(1));
                    return data;
                } else {
                    data.put("Submit-Url", bppUrl + actionUrl);
                    return data;
                }
            }
        }
        data.put("Submit-Url", formUrl);
        return data;
    }
}