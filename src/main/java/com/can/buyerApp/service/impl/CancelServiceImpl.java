package com.can.buyerApp.service.impl;

import com.can.buyerApp.service.CancelService;
import com.can.buyerApp.repository.ContextRepository;
import com.can.buyerApp.repository.CancelReasonRepo;
import com.can.buyerApp.webclient.OndcWebClient;
import com.can.buyerApp.request.CancelRequest;
import com.can.buyerApp.masterentity.CancelReason;
import com.can.buyerApp.entity.ContextEntity;
import com.can.buyerApp.utils.DateTimeUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static com.can.buyerApp.constants.PreConstants.CANCEL;
import static com.can.buyerApp.constants.PreConstants.CITY_CODE;
