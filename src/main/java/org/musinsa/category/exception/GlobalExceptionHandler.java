package org.musinsa.category.exception;

import org.musinsa.category.api.dto.ApiResponse;
import org.musinsa.category.exception.dto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getErrorCode(), ex.getMessage());
        ApiResponse<Object> response = new ApiResponse<>(errorDetails);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}

