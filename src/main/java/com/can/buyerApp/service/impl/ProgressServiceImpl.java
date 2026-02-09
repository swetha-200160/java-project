//package com.example.BuyerApp.service.impl;
//
//import dto.com.can.buyerApp.PolicyDocumentsDTO;
//import dto.com.can.buyerApp.ProviderDTO;
//import dto.com.can.buyerApp.SearchCatalogDTO;
//import com.example.BuyerApp.entity.*;
//import com.example.BuyerApp.repository.*;
//import com.example.BuyerApp.service.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@Slf4j
//public class ProgressServiceImpl implements ProgressService {
//
//    private final ProgressRepository progressRepository;
//    private final ContextRepository contextRepository;
//    private final SearchCatalogRepository searchCatalogRepository;
//    private final OnSearchService onSearchService;
//    private final OnSelectService onSelectService;
//    private final ProposerDetailsService proposerDetailsService;
//    private final NomineeDetailsService nomineeDetailsService;
//    private final OnConfirmService onConfirmService;
//    private final OnSelectRepository onSelectRepository;
//     private final ProposerDetailsRepository proposerDetailsRepository;
//     private final NomineeDetailsRepository nomineeDetailsRepository;
//     private final MotorDocumentRepository documentRepository;
//
//    public ProgressServiceImpl(ProgressRepository progressRepository, ContextRepository contextRepository, SearchCatalogRepository searchCatalogRepository, OnSearchService onSearchService, OnSelectService onSelectService, ProposerDetailsService proposerDetailsService, NomineeDetailsService nomineeDetailsService, OnConfirmService onConfirmService, OnSelectRepository onSelectRepository, ProposerDetailsRepository proposerDetailsRepository, NomineeDetailsRepository nomineeDetailsRepository, MotorDocumentRepository documentRepository) {
//        this.progressRepository = progressRepository;
//        this.contextRepository = contextRepository;
//        this.searchCatalogRepository = searchCatalogRepository;
//        this.onSearchService = onSearchService;
//        this.onSelectService = onSelectService;
//        this.proposerDetailsService = proposerDetailsService;
//        this.nomineeDetailsService = nomineeDetailsService;
//        this.onConfirmService = onConfirmService;
//        this.onSelectRepository = onSelectRepository;
//        this.proposerDetailsRepository = proposerDetailsRepository;
//        this.nomineeDetailsRepository = nomineeDetailsRepository;
//        this.documentRepository = documentRepository;
//    }
//
//    @Override
//    public ResponseEntity<?> getUserStatus(Long userId) {
//        log.info("Fetching user status for userId: {}", userId);
//        Optional<Progress> progressOpt = progressRepository.findLatestByUserId(userId);
//        if (progressOpt.isEmpty()) {
//            log.warn("Invalid User Id: {}", userId);
//            return ResponseEntity.badRequest().body("Invalid User Id");
//        }
//
//        Progress progress = progressOpt.get();
//        String status = progress.getStatus();
//        String transactionId = progress.getTransactionId();
//        log.info("User status: {} for transactionId: {}", status, transactionId);
//        return switch (status) {
//            case "ON_SEARCH_1" -> handleOnSearch1(transactionId);
//            case "ON_SEARCH_2" -> handleOnSearch2(transactionId);
//            case "ON_SELECT" -> handleOnSelect(transactionId);
//            case "ON_INIT_1" -> handleOnInit1(transactionId);
//            case "ON_INIT_2" -> handleOnInit2(transactionId);
//            case "ON_CONFIRM" -> handleOnConfirm(transactionId);
//            default -> ResponseEntity.ok("Invalid status");
//        };
//    }
//
//    private ResponseEntity<?> handleOnSearch1(String transactionId) {
//        log.info("Handling ON_SEARCH_1 for transactionId: {}", transactionId);
//        ContextEntity selectedContext = contextRepository.findByTransactionAndIsSelected(transactionId);
//
//        if (selectedContext == null) {
//            log.info("No selected context found for transactionId: {}. Fetching contexts...", transactionId);
//            List<ContextEntity> contexts = contextRepository.findByTransactionId(transactionId);
//            if (contexts.isEmpty()) {
//                log.info("No contexts found for transactionId: {}", transactionId);
//                return ResponseEntity.ok(Collections.emptyList());
//            }
//            String msgId = contexts.get(0).getMessage_id();
//            List<ProviderDTO> providers = onSearchService.getproviderList(transactionId, msgId);
//            log.info("Found {} providers for transactionId: {}", providers.size(), transactionId);
//            return ResponseEntity.ok(providers);
//        }
//
//        String providerId = selectedContext.getProviderId();
//        String messageId = selectedContext.getMessage_id();
//        log.info("Found selected context for transactionId: {} with providerId: {}", transactionId, providerId);
//        return onSearchService.getInsuranceCategoryByTransactionId(transactionId, providerId, messageId);
//    }
//
//    private ResponseEntity<?> handleOnSearch2(String transactionId) {
//        log.debug("Handling ON_SEARCH_2 for transactionId: {}", transactionId);
//        List<SearchCatalog> catalogs = searchCatalogRepository.findTop2ByTransactionIdOrderByCreatedAtDesc(transactionId);
//        if (catalogs.isEmpty()) return ResponseEntity.ok(Collections.emptyList());
//
//        String messageId = catalogs.get(0).getMessageId();
//        List<SearchCatalogDTO> result = onSearchService.getCatalogs(transactionId, messageId);
//        return ResponseEntity.ok(result);
//    }
//
//    private ResponseEntity<?> handleOnSelect(String transactionId) {
//        log.debug("Handling ON_SELECT for transactionId: {}", transactionId);
//        OnSelectEntity onSelect = onSelectRepository.findByTransactionId(transactionId);
//        if (onSelect == null) return ResponseEntity.ok("Quote not found");
//
//        return onSelectService.getQuoteByTransactionId(transactionId, onSelect.getMessageId());
//    }
//
//    private ResponseEntity<?> handleOnInit1(String transactionId) {
//        log.debug("Handling ON_INIT_1 for transactionId: {}", transactionId);
//        List<ProposerDetails> proposer = proposerDetailsRepository.findTop1ByTransactionIdOrderByCreatedAtDesc(transactionId);
//        if (proposer.isEmpty()) return ResponseEntity.ok("Proposer not found");
//
//        return proposerDetailsService.getProposerTransactionId(transactionId, proposer.get(0).getMessageId());
//    }
//
//    private ResponseEntity<?> handleOnInit2(String transactionId) {
//        log.debug("Handling ON_INIT_2 for transactionId: {}", transactionId);
//        List<NomineeDetails> nominee = nomineeDetailsRepository.findTop1ByTransactionIdOrderByCreatedAtDesc(transactionId);
//        if (nominee.isEmpty()) return ResponseEntity.ok("Nominee not found");
//
//        return nomineeDetailsService.getNomineeTransactionId(transactionId, nominee.get(0).getMessageId());
//    }
//
//    private ResponseEntity<?> handleOnConfirm(String transactionId) {
//        log.debug("Handling ON_CONFIRM for transactionId: {}", transactionId);
//        List<MotorPolicyDocuments> docs = documentRepository.findTop3ByTransactionIdOrderByCreatedAtDesc(transactionId);
//        if (docs.isEmpty()) return ResponseEntity.ok("Documents not found");
//
//        String messageId = docs.get(0).getMessageId();
//        List<PolicyDocumentsDTO> result = onConfirmService.getDocumentsByTransactionId(transactionId, messageId);
//        return ResponseEntity.ok(result);
//    }
//
//}
