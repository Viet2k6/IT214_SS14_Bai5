package com.example.sagacombo.controller;
import com.example.sagacombo.model.ComboOrder;
import com.example.sagacombo.model.ComboOrderRequest;
import com.example.sagacombo.orchestrator.ComboOrderOrchestrator;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/combo")
@RequiredArgsConstructor
public class ComboController {
    private final ComboOrderOrchestrator orchestrator;

    @PostMapping("/book")
    public ComboOrder bookCombo(@RequestParam(defaultValue = "SUCCESS") String scenario) {
        ComboOrderRequest request = new ComboOrderRequest();
        request.setCustomerName("Nguyen Van A");
        request.setFlightNumber("VN-123");
        request.setFlightPrice(1500000);
        request.setHotelName("Vinpearl");
        request.setHotelPrice(2000000);
        request.setPaymentCard("VISA-1234");
        request.setScenarioMode(scenario);
        return orchestrator.executeComboOrder(request);
    }
    
    @GetMapping("/orders")
    public Map<String, ComboOrder> getAllOrders() {
        return orchestrator.getAllOrders();
    }
}
