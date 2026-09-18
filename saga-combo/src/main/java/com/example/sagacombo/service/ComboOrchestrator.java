package com.example.sagacombo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComboOrchestrator {

    @Autowired
    private FlightService flightService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private PaymentService paymentService;

    public String bookCombo(String scenario) {
        System.out.println("\n========== BẮT ĐẦU TRANSACTION ĐẶT COMBO ==========");

        // Bước 1: Đặt vé máy bay
        boolean flightOk = flightService.bookFlight(scenario);
        if (!flightOk) {
            return "Thất bại: Không thể đặt vé máy bay.";
        }

        // Bước 2: Đặt khách sạn (Có cơ chế Retry và Timeout)
        boolean hotelOk = false;
        int maxRetries = 3;
        for (int i = 1; i <= maxRetries; i++) {
            try {
                hotelOk = hotelService.bookHotel(scenario);
                break; // Thành công thì thoát vòng lặp Retry
            } catch (RuntimeException | InterruptedException e) {
                System.out.println("[Orchestrator] Lỗi khi gọi HotelService: " + e.getMessage());
                if (i == maxRetries) {
                    System.out.println("[Orchestrator] Đã hết số lần retry. Tiến hành bù trừ (Rollback)!");
                    break;
                }
                System.out.println("[Orchestrator] Đang thử lại (Retry " + i + ")...");
            }
        }

        // Bù trừ nếu khách sạn thất bại (hoặc timeout quá số lần retry)
        if (!hotelOk) {
            System.out.println("[Orchestrator] Hủy giao dịch vì Lỗi Khách Sạn. Đang gọi rollback...");
            flightService.cancelFlight();
            return "Thất bại: Đặt khách sạn lỗi (hoặc timeout). Đã rollback vé máy bay.";
        }

        // Bước 3: Thanh toán
        boolean paymentOk = paymentService.processPayment(scenario);
        if (!paymentOk) {
            System.out.println("[Orchestrator] Hủy giao dịch vì Lỗi Thanh Toán. Đang gọi rollback...");
            hotelService.cancelHotel();
            flightService.cancelFlight();
            return "Thất bại: Lỗi thanh toán. Đã rollback khách sạn và vé máy bay.";
        }

        System.out.println("========== KẾT THÚC TRANSACTION: THÀNH CÔNG ==========\n");
        return "Thành công: Combo chuyến đi đã được đặt thành công!";
    }
}
