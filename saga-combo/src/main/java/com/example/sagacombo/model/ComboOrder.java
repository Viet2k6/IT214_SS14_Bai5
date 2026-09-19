package com.example.sagacombo.model;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ComboOrder {
    private String orderId;
    private String customerName;
    private double totalPrice;
    private OrderStatus status;
    private String scenarioMode;
    
    private FlightBooking flightBooking;
    private HotelBooking hotelBooking;
    private PaymentTransaction paymentTransaction;
    
    private List<SagaLog> sagaLogs = new ArrayList<>();

    public void addSagaLog(String step, String action, SagaStepStatus status, String message) {
        this.sagaLogs.add(new SagaLog(step, action, status, message));
    }
}
