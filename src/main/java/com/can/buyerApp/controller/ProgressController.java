//package com.example.BuyerApp.controller;
//
//import com.example.BuyerApp.service.ProgressService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@RestController
//public class ProgressController {
//
//    @Autowired
//    private ProgressService progressService;
//
//    @GetMapping("/user-status")
//    public ResponseEntity<?> getUserStatus(@RequestParam Long userId){
//        log.info("Fetching progress of user status. user id{}",userId);
//        ResponseEntity<?> userStatus = progressService.getUserStatus(userId);
//        return userStatus;
//
//    }
//}
