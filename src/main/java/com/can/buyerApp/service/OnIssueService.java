package com.can.buyerApp.service;

import com.can.buyerApp.request.OnIssueRequest;
import org.springframework.http.ResponseEntity;

public interface OnIssueService {
    public ResponseEntity<?> saveOnIssueStatus(OnIssueRequest onIssueRequest);

    ResponseEntity<?> getIssueStatusByPolicyId(String policyId);
}
