package org.musinsa.category.exception.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@NoArgsConstructor
public enum ExceptionMessage {
    // 데이터베이스 관련 에러
    DATABASE_ERROR(500, "데이터베이스 오류가 발생했습니다."),
    // 상품 정보 조회 실패
    PRODUCT_NOT_FOUND(404, "상품 정보를 찾을 수 없습니다."),
    // 카테고리 정보 조회 실패
    CATEGORY_NOT_FOUND(404, "카테고리 정보를 찾을 수 없습니다."),
    // 기타 예외
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다.");

    private int code;
    private String message;

    ExceptionMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    ExceptionMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
