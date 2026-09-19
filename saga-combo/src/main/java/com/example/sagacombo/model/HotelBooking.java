package com.example.sagacombo.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelBooking {
    private String bookingId;
    private String hotelName;
    private double price;
    private String status;
}
