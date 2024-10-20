package org.musinsa.category.exception.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDetails {
    private String errorCode;
    private String errorMessage;
    private LocalDateTime timestamp;

    public ErrorDetails(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = LocalDateTime.now();
    }
}
