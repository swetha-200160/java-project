package com.can.buyerApp.service;

import com.can.buyerApp.masterentity.CancelReason;
import com.can.buyerApp.request.OnCancelRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OnCancelService {

    ResponseEntity<?> saveOnCancelRequest(OnCancelRequest request);

    List<CancelReason> getCancelReason();
}
