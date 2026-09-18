package com.example.sagacombo.service;

import org.springframework.stereotype.Service;

@Service
public class HotelService {

    public boolean bookHotel(String scenario) throws InterruptedException {
        System.out.println("[HotelService] Bắt đầu đặt phòng khách sạn...");
        
        if ("TIMEOUT_HOTEL".equalsIgnoreCase(scenario)) {
            System.out.println("[HotelService] Giả lập hệ thống phản hồi chậm (Timeout)...");
            Thread.sleep(3000); // Giả lập mất 3 giây
            throw new RuntimeException("Timeout khi gọi API HotelService");
        }
        
        if ("HOTEL_FAIL".equalsIgnoreCase(scenario)) {
            System.out.println("[HotelService] Lỗi: Hết phòng khách sạn!");
            return false;
        }
        
        System.out.println("[HotelService] Đặt phòng khách sạn thành công.");
        return true;
    }

    public void cancelHotel() {
        System.out.println("[HotelService - COMPENSATE] Đã hủy phòng khách sạn thành công.");
    }
}
