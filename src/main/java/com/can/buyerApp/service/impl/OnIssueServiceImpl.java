package com.can.buyerApp.service.impl;

import com.can.buyerApp.entity.IssueDetails;
import com.can.buyerApp.repository.IssueRepository;
import com.can.buyerApp.request.OnIssueRequest;
import com.can.buyerApp.service.OnIssueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
public class OnIssueServiceImpl implements OnIssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Override
    public ResponseEntity<?> saveOnIssueStatus(OnIssueRequest onIssueRequest) {

        String id = onIssueRequest.getMessage().getIssue().getId();
        IssueDetails issueDetails = issueRepository.findByIssueId(id);
       int size= onIssueRequest.getMessage().getIssue().getIssue_actions().getRespondent_actions().size();
       issueDetails.setMessageId(onIssueRequest.getContext().getMessage_id());
            issueDetails.setStatus(onIssueRequest.getMessage().getIssue().getIssue_actions().getRespondent_actions().get(size-1).getRespondent_action());
            issueDetails.setShortDescription(onIssueRequest.getMessage().getIssue().getIssue_actions().getRespondent_actions().get(size-1).getShort_desc());
            issueDetails.setOrganizationName(onIssueRequest.getMessage().getIssue().getIssue_actions().getRespondent_actions().get(size-1).getUpdated_by().getOrg().getName());
            issueDetails.setResolutionProviderName(onIssueRequest.getMessage().getIssue().getIssue_actions().getRespondent_actions().get(size-1).getUpdated_by().getPerson().getName());
            issueDetails.setResolutionProviderEmail(onIssueRequest.getMessage().getIssue().getIssue_actions().getRespondent_actions().get(size-1).getUpdated_by().getContact().getEmail());
             issueDetails.setResolutionProviderPhoneNo(onIssueRequest.getMessage().getIssue().getIssue_actions().getRespondent_actions().get(size-1).getUpdated_by().getContact().getPhone());
             issueDetails.setUpdatedAt(LocalDateTime.now());
             log.info("Saving issue details :" +issueDetails);
                issueRepository.save(issueDetails);
            log.info("Saved issue details :" +issueDetails);
                return ResponseEntity.ok().body(issueDetails);

    }

    @Override
    public ResponseEntity<?> getIssueStatusByPolicyId(String policyId) {
        log.info("Fetching issue details based on policy id: {}",policyId);
        IssueDetails issueDetails = issueRepository.findByPolicyId(policyId);
        return ResponseEntity.ok().body(issueDetails);
    }
}
