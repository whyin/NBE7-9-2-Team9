package com.backend.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;

    // ResponseCode Status Created인 경우
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(
                ResponseCode.CREATED.getCode(),
                ResponseCode.CREATED.getMessage(),
                data
        );
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                ResponseCode.OK.getCode(),
                ResponseCode.OK.getMessage(),
                data
        );
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(
                ResponseCode.OK.getCode(),
                ResponseCode.OK.getMessage(),
                null
        );
    }

    // custom 메세지 추가
    public static <T> ApiResponse<T> success(T data, String customMessage) {
        return new ApiResponse<>(
                ResponseCode.OK.getCode(),
                customMessage,
                data
        );
    }

    public static <T> ApiResponse<T> error(ResponseCode code) {
        return new ApiResponse<>(
                code.getCode(),
                code.getMessage(),
                null
        );
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );
    }
}
