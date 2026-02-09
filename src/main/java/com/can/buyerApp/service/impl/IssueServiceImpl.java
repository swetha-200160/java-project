//package com.can.buyerApp.service.impl;
//
//import com.can.buyerApp.entity.*;
//import com.can.buyerApp.repository.*;
//import com.can.buyerApp.request.IssueCloseRequest;
//import com.can.buyerApp.request.IssueRequest;
//import com.can.buyerApp.request.IssueStatusRequest;
//import com.can.buyerApp.service.IssueService;
//import com.can.buyerApp.webclient.OndcWebClient;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import java.time.LocalDateTime;
//import java.util.*;
//
//
//@Slf4j
//@Service
//public class IssueServiceImpl implements IssueService {
//
//    private final MotorDocumentRepository documentRepository;
//    private final IssueRepository issueRepository;
//    private final ContextRepository contextRepository;
//    private final OndcWebClient ondcWebClient;
//    private final OnSelectRepository onSelectRepository;
//    private final NomineeDetailsRepository nomineeDetailsRepository;
//
//    public IssueServiceImpl(MotorDocumentRepository documentRepository, IssueRepository issueRepository, ContextRepository contextRepository, OndcWebClient ondcWebClient, OnSelectRepository onSelectRepository, NomineeDetailsRepository nomineeDetailsRepository) {
//        this.documentRepository = documentRepository;
//        this.issueRepository = issueRepository;
//        this.contextRepository = contextRepository;
//        this.ondcWebClient = ondcWebClient;
//        this.onSelectRepository = onSelectRepository;
//        this.nomineeDetailsRepository = nomineeDetailsRepository;
//    }
//
//    @Value("${api.bap.url}")
//    private String apiBapUrl;
//
//
//    @Override
//    public ResponseEntity<?> sendIssueRequest(String domain,String policyId,String status,String category,String subCategory,String shortDescription,String longDescription, String name,String email,String phoneNumber) {
//
//        IssueRequest issuePayload = createIssuePayload(domain,policyId,status,category,subCategory,shortDescription,longDescription,name,email,phoneNumber);
//        ResponseEntity<?> response = ondcWebClient.sendIssue(issuePayload);
//        log.info("Issue Request sent successfully: {}" ,issuePayload);
//        return ResponseEntity.ok(response.getBody());
//
//    }
//
//    @Override
//    public ResponseEntity<?> sendIssueStatus(String issueId) {
//
//        IssueStatusRequest issueStatusRequest = createIssueStatus(issueId);
//        ResponseEntity<?> response = ondcWebClient.issueStatus(issueStatusRequest);
//        log.info("Issue status Request sent successfully: {}" ,issueStatusRequest);
//        return ResponseEntity.ok(response.getBody());
//    }
//
//
//    @Override
//    public ResponseEntity<?> sendIssueClosure(String issueId, String status) {
//
//        IssueCloseRequest issueClosureRequest = createIssueClosure(issueId,status);
//        ResponseEntity<?> response = ondcWebClient.issueClosure(issueClosureRequest);
//        log.info("Issue status closure Request sent successfully: {}" ,issueClosureRequest);
//        return ResponseEntity.ok(response.getBody());
//    }
//
//
//    private IssueRequest createIssuePayload(String domain, String policyId, String status, String category, String subCategory, String shortDescription, String longDescription, String name, String email, String phoneNumber) {
//
//
//        List<PolicyDocuments> policyDetails = documentRepository.findByPolicyId(policyId);
//        String issueId=generateIssueId();
//        String txidId=policyDetails.get(0).getTransactionId();
//
//        List<NomineeDetails> nomineeDetail = nomineeDetailsRepository.findTop1ByTransactionIdOrderByCreatedAtDesc(txidId);
//        OnSelectEntity onSelectDetail = onSelectRepository.findByTransactionId(txidId);
//
//        saveIssueDetails(policyId,status,name,email,phoneNumber,issueId,txidId);
//        String messageId = UUID.randomUUID().toString();
//        ContextEntity contextDetails = contextRepository.findByTransactionAndIsSelected(txidId);
//
//        // Initialize the IssueRequest object and context
//        IssueRequest request = new IssueRequest();
//        IssueRequest.Context context = new IssueRequest.Context();
//        IssueRequest.Context.Location location = new IssueRequest.Context.Location();
//        IssueRequest.Context.Location.Country country = new IssueRequest.Context.Location.Country();
//        IssueRequest.Context.Location.City city = new IssueRequest.Context.Location.City();
//
//        // Set values for the context
//        country.setCode(contextDetails.getLocation_country_code());
//        city.setCode("*");
//        location.setCountry(country);
//        location.setCity(city);
//
//        context.setDomain(domain);
//        context.setLocation(location);
//        context.setAction("issue");
//        context.setVersion(contextDetails.getVersion());
//        context.setBap_uri(apiBapUrl);
//        context.setBap_id(contextDetails.getBap_id());
//        context.setBpp_id(contextDetails.getBpp_id());
//        context.setBpp_uri(contextDetails.getBpp_uri());
//        context.setTransaction_id(txidId);
//        context.setTtl(contextDetails.getTtl());
//        context.setMessage_id(messageId);
//        context.setTimestamp(contextDetails.getTimestamp());
//
//        // Create the message
//        IssueRequest.Message message = new IssueRequest.Message();
//        IssueRequest.Message.Issue issue = new IssueRequest.Message.Issue();
//
//        // Set values for the issue
//        issue.setId(issueId);
//        issue.setCategory(category);
//        issue.setSub_category(subCategory);
//
//        // Set complainant information
//        IssueRequest.Message.Issue.ComplainantInfo complainantInfo = new IssueRequest.Message.Issue.ComplainantInfo();
//        IssueRequest.Message.Issue.ComplainantInfo.Contact contact = new IssueRequest.Message.Issue.ComplainantInfo.Contact();
//        contact.setEmail(email);
//        contact.setPhone(phoneNumber);
//
//        IssueRequest.Message.Issue.ComplainantInfo.Person person = new IssueRequest.Message.Issue.ComplainantInfo.Person();
//        person.setName(name);
//
//        complainantInfo.setContact(contact);
//        complainantInfo.setPerson(person);
//        issue.setComplainant_info(complainantInfo);
//
//        // Set order details
//        IssueRequest.Message.Issue.OrderDetails orderDetails = new IssueRequest.Message.Issue.OrderDetails();
//        orderDetails.setId(policyId);
//        orderDetails.setState("GRANTED");
//        orderDetails.setProvider_id(contextDetails.getProviderId());
//
//        // Set fulfillments
//        List<IssueRequest.Message.Issue.OrderDetails.Fulfillment> fulfillments = new ArrayList<>();
//        IssueRequest.Message.Issue.OrderDetails.Fulfillment fulfillment = new IssueRequest.Message.Issue.OrderDetails.Fulfillment();
//        fulfillment.setId(nomineeDetail.get(0).getFulfillmentId());
//        fulfillment.setState("GRANTED");
//        fulfillments.add(fulfillment);
//        orderDetails.setFulfillments(fulfillments);
//
//        // Set items
//        List<IssueRequest.Message.Issue.OrderDetails.Item> items = new ArrayList<>();
//        IssueRequest.Message.Issue.OrderDetails.Item item = new IssueRequest.Message.Issue.OrderDetails.Item();
//        item.setId(onSelectDetail.getItemId());
//        items.add(item);
//        orderDetails.setItems(items);
//
//        issue.setOrder_details(orderDetails);
//
//        // Set issue description
//        IssueRequest.Message.Issue.Description description = new IssueRequest.Message.Issue.Description();
//        description.setShort_desc(shortDescription);
//        description.setLong_desc(longDescription);
//
//        // Set additional description
//        IssueRequest.Message.Issue.Description.AdditionalDesc additionalDesc = new IssueRequest.Message.Issue.Description.AdditionalDesc();
//        additionalDesc.setUrl(contextDetails.getProviderUrl());
//        additionalDesc.setContent_type("image/png");
//        description.setAdditional_desc(additionalDesc);
//
//        // Set images
//        List<String> images = new ArrayList<>();
//        images.add(contextDetails.getProviderUrl());
//        description.setImages(images);
//
//        issue.setDescription(description);
//
//        // Set source
//        IssueRequest.Message.Issue.Source source = new IssueRequest.Message.Issue.Source();
//        source.setNetwork_participant_id("pramaan.ondc.org/beta/preprod/mock/buyer");
//        source.setType("CONSUMER");
//        issue.setSource(source);
//
//        // Set expected response and resolution times
//        IssueRequest.Message.Issue.ExpectedResponseTime expectedResponseTime = new IssueRequest.Message.Issue.ExpectedResponseTime();
//        expectedResponseTime.setDuration("PT2H");
//        issue.setExpected_response_time(expectedResponseTime);
//
//        IssueRequest.Message.Issue.ExpectedResolutionTime expectedResolutionTime = new IssueRequest.Message.Issue.ExpectedResolutionTime();
//        expectedResolutionTime.setDuration("P1D");
//        issue.setExpected_resolution_time(expectedResolutionTime);
//
//        // Set issue status and type
//        issue.setStatus("OPEN");
//        issue.setIssue_type("ISSUE");
//
//        // Set actions
//        IssueRequest.Message.Issue.IssueActions issueActions = new IssueRequest.Message.Issue.IssueActions();
//        List<IssueRequest.Message.Issue.IssueActions.ComplainantAction> complainantActions = new ArrayList<>();
//        IssueRequest.Message.Issue.IssueActions.ComplainantAction complainantAction = new IssueRequest.Message.Issue.IssueActions.ComplainantAction();
//        complainantAction.setComplainant_action("OPEN");
//        complainantAction.setShort_desc("Complaint created");
//        complainantAction.setUpdated_at(LocalDateTime.now().toString());
//
//        // Initialize UpdatedBy and Contact objects
//        IssueRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy updatedBy = new IssueRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy();
//
//        // Set the Contact object
//        IssueRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy.Contact contactDetails = new IssueRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy.Contact();
//        contactDetails.setPhone(phoneNumber);
//        contactDetails.setEmail(email);
//
//        // Set the Org object
//        IssueRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy.Org org = new IssueRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy.Org();
//        org.setName(contextDetails.getProviderName());
//
//        // Set the Person object
//        IssueRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy.Person updatedByPerson = new IssueRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy.Person();
//        updatedByPerson.setName("Support Desk");
//
//        // Set all the initialized objects into UpdatedBy
//        updatedBy.setOrg(org);
//        updatedBy.setContact(contactDetails);
//        updatedBy.setPerson(updatedByPerson);
//
//        // Set the UpdatedBy in complainantAction
//        complainantAction.setUpdated_by(updatedBy);
//
//        complainantActions.add(complainantAction);
//        issueActions.setComplainant_actions(complainantActions);
//        issue.setIssue_actions(issueActions);
//
//        // Set timestamps for issue
//        issue.setCreated_at(LocalDateTime.now().toString());
//        issue.setUpdated_at(LocalDateTime.now().toString());
//
//        // Set message
//        message.setIssue(issue);
//
//        // Set context and message in the IssueRequest
//        request.setContext(context);
//        request.setMessage(message);
//
//        return request;
//    }
//
//    private void saveIssueDetails(String policyId, String status,  String name, String email, String phoneNumber, String issueId, String txidId) {
//
//        IssueDetails issueDetails=new IssueDetails();
//        issueDetails.setIssueId(issueId);
//        issueDetails.setCustomerName(name);
//        issueDetails.setCustomerEmail(email);
//        issueDetails.setStatus(status);
//        issueDetails.setCustomerPhoneNumber(phoneNumber);
//        issueDetails.setPolicyId(policyId);
//        issueDetails.setTransactionId(txidId);
//        issueDetails.setUpdatedAt(LocalDateTime.now());
//        issueRepository.save(issueDetails);
//    }
//
//
//    public String generateIssueId(){
//        Random random=new Random();
//        int id= 10000 + random.nextInt(90000);
//        String issueId = "IS" + String.valueOf(id);
//        return  issueId;
//    }
//
//
//
//
//
//    private IssueStatusRequest createIssueStatus(String issueId) {
//
//        IssueDetails issueDetails = issueRepository.findByIssueId(issueId);
//
//        String txidId=issueDetails.getTransactionId();
//
//        String messageId = UUID.randomUUID().toString();
//        ContextEntity contextDetails = contextRepository.findByTransactionAndIsSelected(txidId);
//
//        // Initialize the IssueRequest object and context
//        IssueStatusRequest request = new IssueStatusRequest();
//        IssueStatusRequest.Context context = new IssueStatusRequest.Context();
//        IssueStatusRequest.Location location = new IssueStatusRequest.Location();
//        IssueStatusRequest.Country country = new IssueStatusRequest.Country();
//        IssueStatusRequest.City city = new IssueStatusRequest.City();
//
//        // Set values for the context
//        country.setCode(contextDetails.getLocation_country_code());
//        city.setCode("*");
//        location.setCountry(country);
//        location.setCity(city);
//
//        context.setDomain(contextDetails.getDomain());
//        context.setLocation(location);
//        context.setAction("issue_status");
//        context.setVersion(contextDetails.getVersion());
//        context.setBap_uri(apiBapUrl);
//        context.setBap_id(contextDetails.getBap_id());
//        context.setBpp_id(contextDetails.getBpp_id());
//        context.setBpp_uri(contextDetails.getBpp_uri());
//        context.setTransaction_id(txidId);
//        context.setTtl(contextDetails.getTtl());
//        context.setMessage_id(messageId);
//        context.setTimestamp(contextDetails.getTimestamp());
//
//        request.setContext(context);
//
//        IssueStatusRequest.Message message = new IssueStatusRequest.Message();
//        message.setIssue_id(issueId);
//        request.setMessage(message);
//
//        return request;
//    }
//
//
//    private IssueCloseRequest createIssueClosure(String issueId, String status) {
//
//        IssueDetails issueDetails = issueRepository.findByIssueId(issueId);
//
//        String txidId=issueDetails.getTransactionId();
//
//        String messageId = UUID.randomUUID().toString();
//        ContextEntity contextDetails = contextRepository.findByTransactionAndIsSelected(txidId);
//
//        IssueCloseRequest issueCloseRequest = new IssueCloseRequest();
//
//        IssueCloseRequest.Context context = new IssueCloseRequest.Context();
//        context.setDomain(contextDetails.getDomain());
//
//        IssueCloseRequest.Context.Location location = new IssueCloseRequest.Context.Location();
//        IssueCloseRequest.Context.Location.Country country = new IssueCloseRequest.Context.Location.Country();
//        country.setCode(contextDetails.getLocation_country_code());
//        IssueCloseRequest.Context.Location.City city = new IssueCloseRequest.Context.Location.City();
//        city.setCode("*");
//        location.setCountry(country);
//        location.setCity(city);
//
//        context.setLocation(location);
//        context.setAction("issue");
//        context.setVersion(contextDetails.getVersion());
//        context.setBap_uri(apiBapUrl);
//        context.setBap_id(contextDetails.getBap_id());
//        context.setBpp_id(contextDetails.getBpp_id());
//        context.setBpp_uri(contextDetails.getBpp_uri());
//        context.setTransaction_id(txidId);
//        context.setTtl(contextDetails.getTtl());
//        context.setMessage_id(messageId);
//        context.setTimestamp(contextDetails.getTimestamp());
//
//        // Create Issue
//        IssueCloseRequest.Message message = new IssueCloseRequest.Message();
//        IssueCloseRequest.Message.Issue issue = new IssueCloseRequest.Message.Issue();
//        issue.setId(issueId);
//        issue.setCreated_at(LocalDateTime.now().toString());
//        issue.setUpdated_at(LocalDateTime.now().toString());
//        issue.setStatus(status);
//        issue.setRating("THUMBS-UP");
//
//        // Create IssueActions
//        IssueCloseRequest.Message.Issue.IssueActions issueActions = new IssueCloseRequest.Message.Issue.IssueActions();
//
//        // Create ComplainantActions
//        IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction complainantAction1 = new IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction();
//        complainantAction1.setComplainant_action("OPEN");
//        complainantAction1.setShort_desc("Complaint created");
//        complainantAction1.setUpdated_at(issueDetails.getUpdatedAt().toString());
//        IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction complainantAction2 = new IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction();
//        complainantAction2.setComplainant_action("CLOSE");
//        complainantAction2.setShort_desc("Complaint closed");
//        complainantAction2.setUpdated_at(LocalDateTime.now().toString());
//
//        // Create UpdatedBy
//        IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy updatedBy1 = new IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy();
//        updatedBy1.setOrg(new IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy.Org());
//        updatedBy1.getOrg().setName(contextDetails.getProviderName());
//        updatedBy1.setContact(new IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy.Contact());
//        updatedBy1.getContact().setPhone(issueDetails.getResolutionProviderPhoneNo());
//        updatedBy1.getContact().setEmail(issueDetails.getResolutionProviderEmail());
//        updatedBy1.setPerson(new IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy.Person());
//        updatedBy1.getPerson().setName(issueDetails.getResolutionProviderName());
//
//        IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy updatedBy2 = new IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy();
//        updatedBy2.setOrg(new IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy.Org());
//        updatedBy2.getOrg().setName(contextDetails.getProviderName());
//        updatedBy2.setContact(new IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy.Contact());
//        updatedBy2.getContact().setPhone(issueDetails.getResolutionProviderPhoneNo());
//        updatedBy2.getContact().setEmail(issueDetails.getResolutionProviderEmail());
//        updatedBy2.setPerson(new IssueCloseRequest.Message.Issue.IssueActions.ComplainantAction.UpdatedBy.Person());
//        updatedBy2.getPerson().setName(issueDetails.getResolutionProviderName());
//
//        complainantAction1.setUpdated_by(updatedBy1);
//        complainantAction2.setUpdated_by(updatedBy2);
//
//        issueActions.setComplainant_actions(Arrays.asList(complainantAction1, complainantAction2));
//        issue.setIssue_actions(issueActions);
//        message.setIssue(issue);
//
//        // Create IssueCloseRequest
//
//        issueCloseRequest.setContext(context);
//        issueCloseRequest.setMessage(message);
//        return issueCloseRequest;
//    }
//
//}
