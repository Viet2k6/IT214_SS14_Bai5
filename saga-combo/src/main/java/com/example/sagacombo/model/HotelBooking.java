package com.example.sagacombo.model;
import lombok.Data;

@Data
public class HotelBooking {
    private String bookingId;
    private String hotelName;
    private double price;
    private String status;
}
