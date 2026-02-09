//package com.can.buyerApp.controller;
//
//import com.can.buyerApp.request.OnIssueRequest;
//import com.can.buyerApp.service.IssueService;
//import com.can.buyerApp.service.OnIssueService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//
//
//@Slf4j
//@RestController
//public class IssueController {
//
//    private final IssueService issueService;
//    private final OnIssueService onIssueService;
//
//    public IssueController(IssueService issueService, OnIssueService onIssueService) {
//        this.issueService = issueService;
//        this.onIssueService=onIssueService;
//    }
//
//    @PostMapping("/issue")
//    public ResponseEntity<?> createIssueRequest(@RequestParam(required = false) String domain,
//                                                @RequestParam(required = false) String policyId,
//                                                @RequestParam(required = false) String status,
//                                                @RequestParam(required = false) String category,
//                                                @RequestParam(required = false) String subCategory,
//                                                @RequestParam(required = false) String shortDescription,
//                                                @RequestParam(required = false) String longDescription,
//                                                @RequestParam(required = false) String name,
//                                                @RequestParam(required = false) String email,
//                                                @RequestParam(required = false) String phoneNumber,
//                                                @RequestParam(required = false) String issueId) {
//
//        if (issueId==null && status.equalsIgnoreCase("OPEN")){
//            ResponseEntity<?> response = issueService.sendIssueRequest(domain, policyId, status, category, subCategory, shortDescription, longDescription, name, email, phoneNumber);
//            return ResponseEntity.ok().body(response);
//        }
//        log.info("sending issue closure"+ issueId);
//       ResponseEntity<?> response = issueService.sendIssueClosure(issueId,status);
//        return ResponseEntity.ok().body(response);
//    }
//
//
//    @PostMapping("/on_issue")
//    public ResponseEntity<?> saveOnIssueResponse(@RequestBody OnIssueRequest onIssueRequest){
//        log.info("received On issue request"+ onIssueRequest);
//        return onIssueService.saveOnIssueStatus(onIssueRequest);
//    }
//
//    @PostMapping("/issue_status")
//    public ResponseEntity<?> sendIssueStatus(@RequestParam String issueId){
//        log.info("sending issue status"+ issueId);
//        return issueService.sendIssueStatus(issueId);
//    }
//
//    @PostMapping("/on_issue_status")
//    public ResponseEntity<?> saveOnIssueStatusResponse(@RequestBody OnIssueRequest onIssueRequest){
//        log.info("received On issue status request"+ onIssueRequest);
//        return  onIssueService.saveOnIssueStatus(onIssueRequest);
//    }
//
//    @GetMapping("/get-issue-status")
//    public ResponseEntity<?> getIssueStatus(@RequestParam String policyId){
//        return onIssueService.getIssueStatusByPolicyId(policyId);
//    }
//
//}
//
