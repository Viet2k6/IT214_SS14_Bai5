package com.example.sagacombo.model;
import lombok.Data;

@Data
public class FlightBooking {
    private String bookingId;
    private String flightNumber;
    private double price;
    private String status;
}
