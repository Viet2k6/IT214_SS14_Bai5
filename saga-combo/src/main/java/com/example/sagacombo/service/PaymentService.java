package com.example.sagacombo.service;
import com.example.sagacombo.model.PaymentTransaction;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class PaymentService {
    public PaymentTransaction processPayment(String orderId, double amount, String cardInfo, String scenarioMode) throws Exception {
        System.out.println("[PaymentService] Bắt đầu xử lý thanh toán cho Order: " + orderId);
        if ("PAYMENT_FAIL".equalsIgnoreCase(scenarioMode)) {
            throw new Exception("Thẻ " + cardInfo + " bị từ chối hoặc không đủ số dư.");
        }
        PaymentTransaction tx = new PaymentTransaction();
        tx.setTransactionId("TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        tx.setAmount(amount);
        tx.setStatus("SUCCESS");
        return tx;
    }
    public void refundPayment(String transactionId, String orderId) {
        System.out.println("[PaymentService - COMPENSATE] Đã hoàn tiền cho giao dịch " + transactionId + " của Order: " + orderId);
    }
}
