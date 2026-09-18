package com.example.sagacombo.controller;

import com.example.sagacombo.service.ComboOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/combo")
public class ComboController {

    @Autowired
    private ComboOrchestrator comboOrchestrator;

    @PostMapping("/book")
    public String bookCombo(@RequestParam(defaultValue = "SUCCESS") String scenario) {
        return comboOrchestrator.bookCombo(scenario);
    }
}
