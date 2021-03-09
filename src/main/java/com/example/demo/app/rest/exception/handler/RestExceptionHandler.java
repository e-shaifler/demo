package com.example.demo.app.rest.exception.handler;

import com.example.demo.app.rest.exception.BaseLogicRestException;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;


@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(BaseLogicRestException.class)
    public ResponseEntity<MessageError> handleBaseLogicRestException(BaseLogicRestException ex) {
        return getResponseEntity(ex.getMessage(), ex.getStatus());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<MessageError> handleBindException(BindException ex) {
        String errors = ex.getAllErrors().stream()
                .map(oe -> oe.getDefaultMessage()).collect(Collectors.joining("; "));
        return getResponseEntity(errors, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MessageError> handleBindException(ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage()).collect(Collectors.joining("; "));
        return getResponseEntity(errors, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageError> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return getResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<MessageError> getResponseEntity(String message, HttpStatus httpStatus){
        MessageError messageError = MessageError.builder()
                .message(message)
                .status(String.valueOf(httpStatus.value())).build();
        return new ResponseEntity<>(messageError, httpStatus);
    }

    @Getter
    @Builder
    public static class MessageError {
        private final String status;
        private final String message;
    }
}
