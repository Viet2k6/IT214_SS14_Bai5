package com.example.sagacombo.service;
import com.example.sagacombo.model.FlightBooking;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class FlightService {
    public FlightBooking bookFlight(String orderId, String flightNumber, double price, String scenarioMode) throws Exception {
        System.out.println("[FlightService] Bắt đầu đặt vé máy bay cho Order: " + orderId);
        if ("FLIGHT_FAIL".equalsIgnoreCase(scenarioMode)) {
            throw new Exception("Hết vé máy bay chuyến " + flightNumber);
        }
        FlightBooking booking = new FlightBooking();
        booking.setBookingId("FL-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        booking.setFlightNumber(flightNumber);
        booking.setPrice(price);
        booking.setStatus("CONFIRMED");
        return booking;
    }
    public void cancelFlightBooking(String bookingId, String orderId) {
        System.out.println("[FlightService - COMPENSATE] Đã hủy vé máy bay " + bookingId + " của Order: " + orderId);
    }
}
