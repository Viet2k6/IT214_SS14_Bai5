package com.example.sagacombo.orchestrator;
import com.example.sagacombo.model.*;
import com.example.sagacombo.service.FlightService;
import com.example.sagacombo.service.HotelService;
import com.example.sagacombo.service.PaymentService;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Component
public class ComboOrderOrchestrator {
    private final FlightService flightService;
    private final HotelService hotelService;
    private final PaymentService paymentService;
    private final Map<String, ComboOrder> orderDatabase = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final long TIMEOUT_SECONDS = 2;
    private static final int MAX_RETRIES = 1;

    public ComboOrderOrchestrator(FlightService flightService, HotelService hotelService, PaymentService paymentService) {
        this.flightService = flightService;
        this.hotelService = hotelService;
        this.paymentService = paymentService;
    }

    public ComboOrder executeComboOrder(ComboOrderRequest request) {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ComboOrder order = new ComboOrder();
        order.setOrderId(orderId);
        order.setCustomerName(request.getCustomerName());
        order.setTotalPrice(request.getFlightPrice() + request.getHotelPrice());
        order.setScenarioMode(request.getScenarioMode());
        order.setStatus(OrderStatus.PROCESSING);
        orderDatabase.put(orderId, order);

        System.out.println("================================================================================");
        System.out.println("[SAGA ORCHESTRATOR] BẮT ĐẦU GIAO DỊCH COMBO. Order ID: " + orderId + ", Khách hàng: " + request.getCustomerName());
        System.out.println("================================================================================");
        order.addSagaLog("Saga Orchestration", "START", SagaStepStatus.SUCCESS, "Khởi tạo Saga Orchestration");

        FlightBooking flightBooking = null;
        try {
            order.addSagaLog("Step 1: Flight Booking", "FORWARD", SagaStepStatus.NOT_STARTED, "Gửi yêu cầu đặt vé máy bay");
            flightBooking = flightService.bookFlight(orderId, request.getFlightNumber(), request.getFlightPrice(), request.getScenarioMode());
            order.setFlightBooking(flightBooking);
            order.addSagaLog("Step 1: Flight Booking", "FORWARD", SagaStepStatus.SUCCESS, "Đặt vé thành công: " + flightBooking.getBookingId());
        } catch (Exception e) {
            System.err.println("[SAGA] Bước 1 thất bại: " + e.getMessage());
            order.addSagaLog("Step 1: Flight Booking", "FORWARD", SagaStepStatus.FAILED, "Lỗi: " + e.getMessage());
            order.setStatus(OrderStatus.FAILED_FLIGHT);
            return order;
        }

        HotelBooking hotelBooking = null;
        try {
            order.addSagaLog("Step 2: Hotel Booking", "FORWARD", SagaStepStatus.NOT_STARTED, "Gửi yêu cầu đặt phòng (Timeout: " + TIMEOUT_SECONDS + "s)");
            hotelBooking = executeHotelBookingWithTimeoutAndRetry(order, request);
            order.setHotelBooking(hotelBooking);
            order.addSagaLog("Step 2: Hotel Booking", "FORWARD", SagaStepStatus.SUCCESS, "Đặt phòng thành công: " + hotelBooking.getBookingId());
        } catch (TimeoutException te) {
            System.err.println("[SAGA] Bước 2 BỊ TIMEOUT sau khi đã hết số lần retry!");
            order.addSagaLog("Step 2: Hotel Booking", "FORWARD", SagaStepStatus.TIMED_OUT, "Đối tác phản hồi chậm. Kích hoạt bù trừ!");
            order.setStatus(OrderStatus.FAILED_TIMEOUT);
            compensate(order, false, false, true);
            return order;
        } catch (Exception e) {
            System.err.println("[SAGA] Bước 2 THẤT BẠI: " + e.getMessage());
            order.addSagaLog("Step 2: Hotel Booking", "FORWARD", SagaStepStatus.FAILED, "Lỗi: " + e.getMessage() + ". Kích hoạt bù trừ!");
            order.setStatus(OrderStatus.FAILED_HOTEL);
            compensate(order, false, false, true);
            return order;
        }

        PaymentTransaction paymentTransaction = null;
        try {
            order.addSagaLog("Step 3: Payment", "FORWARD", SagaStepStatus.NOT_STARTED, "Gửi yêu cầu thanh toán");
            paymentTransaction = paymentService.processPayment(orderId, order.getTotalPrice(), request.getPaymentCard(), request.getScenarioMode());
            order.setPaymentTransaction(paymentTransaction);
            order.addSagaLog("Step 3: Payment", "FORWARD", SagaStepStatus.SUCCESS, "Thanh toán thành công: " + paymentTransaction.getTransactionId());
        } catch (Exception e) {
            System.err.println("[SAGA] Bước 3 THẤT BẠI: " + e.getMessage());
            order.addSagaLog("Step 3: Payment", "FORWARD", SagaStepStatus.FAILED, "Lỗi: " + e.getMessage() + ". Kích hoạt bù trừ!");
            order.setStatus(OrderStatus.FAILED_PAYMENT);
            compensate(order, false, true, true);
            return order;
        }

        order.setStatus(OrderStatus.SUCCESS);
        order.addSagaLog("Step 4: Finalize", "COMPLETE", SagaStepStatus.SUCCESS, "Giao dịch thành công rực rỡ!");
        System.out.println("================================================================================");
        System.out.println("[SAGA ORCHESTRATOR] GIAO DỊCH COMBO HOÀN TẤT THÀNH CÔNG! Order ID: " + orderId);
        System.out.println("================================================================================");
        return order;
    }

    private HotelBooking executeHotelBookingWithTimeoutAndRetry(ComboOrder order, ComboOrderRequest request) throws Exception {
        int attempts = 0;
        Exception lastException = null;
        while (attempts <= MAX_RETRIES) {
            attempts++;
            try {
                if (attempts > 1) {
                    System.out.println("[SAGA RETRY] Thử lại lần " + attempts + " gọi Hotel Service...");
                    order.addSagaLog("Step 2: Hotel Booking Retry", "RETRY", SagaStepStatus.NOT_STARTED, "Thử lại lần " + attempts);
                }
                Future<HotelBooking> future = executorService.submit(() ->
                        hotelService.bookHotel(order.getOrderId(), request.getHotelName(), request.getHotelPrice(), request.getScenarioMode())
                );
                return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (TimeoutException te) {
                lastException = te;
                System.out.println("[SAGA] Lần gọi thứ " + attempts + " bị Timeout");
                if (attempts > MAX_RETRIES) throw te;
            } catch (ExecutionException ee) {
                Throwable cause = ee.getCause();
                if (cause instanceof Exception) throw (Exception) cause;
                throw ee;
            }
        }
        if (lastException != null) throw lastException;
        throw new RuntimeException("Không thể đặt phòng sau các lần thử");
    }

    private void compensate(ComboOrder order, boolean rollbackPayment, boolean rollbackHotel, boolean rollbackFlight) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("[SAGA COMPENSATION] BẮT ĐẦU BÙ TRỪ CHO ORDER ID: " + order.getOrderId());
        if (rollbackPayment && order.getPaymentTransaction() != null) {
            paymentService.refundPayment(order.getPaymentTransaction().getTransactionId(), order.getOrderId());
            order.addSagaLog("Compensate Payment", "COMPENSATE", SagaStepStatus.COMPENSATED, "Đã hoàn tiền.");
        }
        if (rollbackHotel && order.getHotelBooking() != null) {
            hotelService.cancelHotelBooking(order.getHotelBooking().getBookingId(), order.getOrderId());
            order.addSagaLog("Compensate Hotel", "COMPENSATE", SagaStepStatus.COMPENSATED, "Đã hủy phòng.");
        }
        if (rollbackFlight && order.getFlightBooking() != null) {
            flightService.cancelFlightBooking(order.getFlightBooking().getBookingId(), order.getOrderId());
            order.addSagaLog("Compensate Flight", "COMPENSATE", SagaStepStatus.COMPENSATED, "Đã hủy vé máy bay.");
        }
        System.out.println("[SAGA COMPENSATION] KẾT THÚC BÙ TRỪ CHO ORDER ID: " + order.getOrderId());
        System.out.println("--------------------------------------------------------------------------------");
    }

    public Map<String, ComboOrder> getAllOrders() {
        return orderDatabase;
    }
}
