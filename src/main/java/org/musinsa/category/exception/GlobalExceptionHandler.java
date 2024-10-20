package org.musinsa.category.exception;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.musinsa.category.api.dto.ApiResponse;
import org.musinsa.category.api.dto.ApiResponseDto;
import org.musinsa.category.exception.dto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getErrorCode(), ex.getMessage());
        ApiResponse<Object> response = new ApiResponse<>(errorDetails);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(CategoryException.class)
//    @ResponseBody
//    public ResponseEntity<CustomErrorResponse> handleCategoryException(CategoryException ex) {
//        CustomErrorResponse errorResponse = new CustomErrorResponse();
//        errorResponse.setTimestamp(LocalDateTime.now());
//        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
//        errorResponse.setError("Product Not Found");
//        errorResponse.setMessage(ex.getMessage());
//        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponseDto> handleException(Exception e) {
//        ApiResponseDto response = new ApiResponseDto(false, "서버 오류: " + e.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponseDto> handleValidationException(MethodArgumentNotValidException e) {
//        ApiResponseDto response = new ApiResponseDto(false, "입력값 오류: " + Objects.requireNonNull(
//                e.getBindingResult().getFieldError()).getDefaultMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }
}

@Data
@AllArgsConstructor
class CustomErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    public CustomErrorResponse() {

    }
}
