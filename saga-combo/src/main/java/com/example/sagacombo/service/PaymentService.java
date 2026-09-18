package com.example.sagacombo.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public boolean processPayment(String scenario) {
        System.out.println("[PaymentService] Bắt đầu xử lý thanh toán...");
        if ("PAYMENT_FAIL".equalsIgnoreCase(scenario)) {
            System.out.println("[PaymentService] Lỗi: Tài khoản không đủ số dư hoặc giao dịch bị từ chối!");
            return false;
        }
        System.out.println("[PaymentService] Thanh toán thành công.");
        return true;
    }
}
