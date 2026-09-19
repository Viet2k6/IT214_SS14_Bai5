package com.example.sagacombo.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightBooking {
    private String bookingId;
    private String flightNumber;
    private double price;
    private String status;
}
