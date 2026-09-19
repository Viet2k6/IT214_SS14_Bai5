package com.example.sagacombo.model;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SagaLog {
    private String step;
    private String action;
    private SagaStepStatus status;
    private String message;
    private LocalDateTime timestamp;

    public SagaLog(String step, String action, SagaStepStatus status, String message) {
        this.step = step;
        this.action = action;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
