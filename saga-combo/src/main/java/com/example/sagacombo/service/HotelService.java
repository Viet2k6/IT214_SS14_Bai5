package com.example.sagacombo.service;
import com.example.sagacombo.model.HotelBooking;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class HotelService {
    public HotelBooking bookHotel(String orderId, String hotelName, double price, String scenarioMode) throws Exception {
        System.out.println("[HotelService] Bắt đầu đặt phòng cho Order: " + orderId);
        if ("TIMEOUT_HOTEL".equalsIgnoreCase(scenarioMode)) {
            System.out.println("[HotelService] Giả lập hệ thống phản hồi chậm...");
            Thread.sleep(3000);
        }
        if ("HOTEL_FAIL".equalsIgnoreCase(scenarioMode)) {
            throw new Exception("Khách sạn " + hotelName + " đã hết phòng trống.");
        }
        HotelBooking booking = new HotelBooking();
        booking.setBookingId("HT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        booking.setHotelName(hotelName);
        booking.setPrice(price);
        booking.setStatus("CONFIRMED");
        return booking;
    }
    public void cancelHotelBooking(String bookingId, String orderId) {
        System.out.println("[HotelService - COMPENSATE] Đã hủy phòng khách sạn " + bookingId + " của Order: " + orderId);
    }
}
