package com.example.sagacombo.model;
import lombok.Data;

@Data
public class ComboOrderRequest {
    private String customerName;
    private String flightNumber;
    private double flightPrice;
    private String hotelName;
    private double hotelPrice;
    private String paymentCard;
    private String scenarioMode;
}
