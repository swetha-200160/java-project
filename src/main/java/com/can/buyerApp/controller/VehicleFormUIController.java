package com.can.buyerApp.controller;

import com.can.buyerApp.service.MotorOnSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/ui")
public class VehicleFormUIController {

    private final MotorOnSearchService motorOnSearchService;

    public VehicleFormUIController(MotorOnSearchService motorOnSearchService) {
        this.motorOnSearchService = motorOnSearchService;
    }

    @GetMapping("/vehicle-form")
    public String showVehicleForm(
            @RequestParam String transactionId,
            @RequestParam String providerId,
            @RequestParam String formUrl,
            @RequestParam String messageId,
            @RequestParam String formId,
            @RequestParam String categoryId,
            Model model) {

        Map<String, String> formData =
                motorOnSearchService.getMotorForm(
                        transactionId, providerId, formUrl, messageId, formId, categoryId);

        model.addAttribute("formHtml", formData.get("HTML"));
        model.addAttribute("sellerSubmitUrl", formData.get("Submit-Url"));

        model.addAttribute("transactionId", transactionId);
        model.addAttribute("messageId", messageId);

        return "vehicle-form"; // maps to templates/vehicle-form.html
    }
}
