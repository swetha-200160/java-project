package com.can.buyerApp.service.impl;


import com.can.buyerApp.entity.ContextEntity;
import com.can.buyerApp.entity.NomineeDetails;
import com.can.buyerApp.entity.OnSelectEntity;
import com.can.buyerApp.exception.TransactionIdNotFoundException;
import com.can.buyerApp.repository.ContextRepository;
import com.can.buyerApp.repository.NomineeDetailsRepository;
import com.can.buyerApp.repository.OnSelectRepository;
import com.can.buyerApp.service.NomineeDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NomineeDetailsServiceImpl implements NomineeDetailsService {

    private final NomineeDetailsRepository nomineeDetailsRepository;
    private final OnSelectRepository onSelectRepository;
    private final RestTemplate restTemplate;
    private final ContextRepository contextRepository;

    public NomineeDetailsServiceImpl(NomineeDetailsRepository nomineeDetailsRepository,OnSelectRepository onSelectRepository,RestTemplate restTemplate,ContextRepository contextRepository) {
        this.nomineeDetailsRepository = nomineeDetailsRepository;
        this.onSelectRepository=onSelectRepository;
        this.restTemplate=restTemplate;
        this.contextRepository=contextRepository;
    }

    @Value("${api.bap.url}")
    private String apiBapUrl;


    @Override
    public ResponseEntity<?> getNomineeTransactionId(String transactionId, String messageId) {

        List<NomineeDetails> nomineeDetailsList = nomineeDetailsRepository.findByTransactionIdAndMessageId(transactionId, messageId);

        if (nomineeDetailsList.isEmpty()) {
            throw new TransactionIdNotFoundException("Transaction ID not found");
        }

        Map<String, Object> data = new LinkedHashMap<>();


        for (NomineeDetails nomineeDetails : nomineeDetailsList) {

            String txId=nomineeDetails.getTransactionId();
            String nomineeForm=nomineeDetails.getNomineeForm();
//            String nomineeUrl= apiBapUrl+"forms?transactionId="+txId+"&formUrl="+nomineeForm;
            String nomineeUrl= "https://ondcpreprod.canvendor.co.in/"+"forms?transactionId="+txId+"&formUrl="+nomineeForm;
            data.put("nomineeId",nomineeDetails.getId());
            data.put("transactionId",nomineeDetails.getTransactionId());
            data.put("messageId",nomineeDetails.getMessageId());
            data.put("email",nomineeDetails.getEmail());
            data.put("phone",nomineeDetails.getPhone());
            data.put("formId",nomineeDetails.getFormId());
            data.put("nomineeForm",nomineeUrl);
            data.put("paymentForm",nomineeDetails.getPaymentForm());
        }

        OnSelectEntity response = onSelectRepository.findByTransactionId(transactionId);

        data.put("Addons",response.getAddOns());
        data.put("breakupDetails",response.getBreakupDetails());
        data.put("totalPrice",response.getTotalPrice());
        log.info("Fetching Nominee form for the transaction Id: " + transactionId);
        return ResponseEntity.ok(data);
    }

    @Override
    public Map<String,String> getForm(String transactionId, String formUrl) {
        Map<String,String> data=new LinkedHashMap<>();
        ResponseEntity<String> response = restTemplate.getForEntity(formUrl, String.class);
        data.put("HTML", response.getBody());

        String html = response.getBody();
        Document doc = Jsoup.parse(html);
        Element form = doc.selectFirst("form");

        if (form != null) {

            String actionUrl =  form.attr("action");
            ContextEntity providerDetails = contextRepository.findByTransactionAndIsSelected(transactionId);
            String bppUrl=providerDetails.getBpp_uri();

            if (!actionUrl.startsWith("http")) {

                if (!bppUrl.endsWith("/") && !actionUrl.startsWith("/")) {
                    data.put("Submit-Url",bppUrl + "/" + actionUrl) ;
                    return data;
                }
                else if (bppUrl.endsWith("/") && actionUrl.startsWith("/")) {
                    data.put("Submit-Url",bppUrl + actionUrl.substring(1)) ;
                    return data;
                }
                else {
                    data.put("Submit-Url",bppUrl + actionUrl) ;
                    return data;
                }
            }
        }
        data.put("Submit-Url",formUrl) ;
        return data;
    }
}
