package com.example.sagacombo.service;

import org.springframework.stereotype.Service;

@Service
public class FlightService {

    public boolean bookFlight(String scenario) {
        System.out.println("[FlightService] Bắt đầu đặt vé máy bay...");
        if ("FLIGHT_FAIL".equalsIgnoreCase(scenario)) {
            System.out.println("[FlightService] Lỗi: Hết vé máy bay!");
            return false;
        }
        System.out.println("[FlightService] Đặt vé máy bay thành công.");
        return true;
    }

    public void cancelFlight() {
        System.out.println("[FlightService - COMPENSATE] Đã hủy vé máy bay thành công.");
    }
}
