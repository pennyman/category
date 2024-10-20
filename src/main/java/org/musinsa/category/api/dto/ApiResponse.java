package org.musinsa.category.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.musinsa.category.exception.dto.ErrorDetails;

@Getter
@Setter
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private ErrorDetails error;

    // 성공 응답을 위한 생성자
    public ApiResponse(T data) {
        this.success = true;
        this.data = data;
        this.message = "Success";
    }

    // 실패 응답을 위한 생성자
    public ApiResponse(ErrorDetails error) {
        this.success = false;
        this.error = error;
        this.message = "Failure";
    }
}
