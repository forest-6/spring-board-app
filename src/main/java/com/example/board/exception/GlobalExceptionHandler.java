package com.example.board.exception;

import com.example.board.dto.error.ClientErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 전역 예외 처리기 (예외 발생 시 JSON으로 응답)
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class) // 모든 예외가 발생하면 이 메서드 실행
    public ResponseEntity<ResponseEntity<Void>> handleException(Exception e) {
        // HTTP 응답 전체를 직접 만드는 객체
        return ResponseEntity.status(500).build();
    }

    @ExceptionHandler(ClientErrorException.class) // 클라이언트 측 에러(4xx)를 공통으로 처리
    public ResponseEntity<ClientErrorResponse> handleClientErrorException(ClientErrorException e) {
        // 예외 객체에 담긴 상태 코드(Status)에 따라 응답을 동적으로 생성
        return new ResponseEntity<>(
                new ClientErrorResponse(e.getStatus(), e.getMessage()), e.getStatus());
    }

}
