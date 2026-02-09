package com.can.buyerApp.repository;

import com.can.buyerApp.entity.IssueDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<IssueDetails,Long> {

    IssueDetails findByIssueId(String issueId);
    IssueDetails findByPolicyId(String policyId);
}
