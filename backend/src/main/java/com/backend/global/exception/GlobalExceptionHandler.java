package com.backend.global.exception;

import com.backend.global.response.ApiResponse;
import com.backend.global.response.ErrorCode;
import com.backend.global.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation Failed : {}", errorMessage);

        ApiResponse<String> apiResponse = new ApiResponse<>(
                ResponseCode.BAD_REQUEST.getCode(),
                ResponseCode.BAD_REQUEST.getMessage(),
                errorMessage
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("Business exception occurred : Code - {}, Message - {}", errorCode, errorCode.getMessage());
        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode);

        return new ResponseEntity<>(apiResponse, e.getErrorCode().getStatus());
    }
}
