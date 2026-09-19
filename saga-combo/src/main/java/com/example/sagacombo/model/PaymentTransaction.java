package com.example.sagacombo.model;
import lombok.Data;

@Data
public class PaymentTransaction {
    private String transactionId;
    private double amount;
    private String status;
}
