package com.example.sagacombo.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {
    private String transactionId;
    private double amount;
    private String status;
}
