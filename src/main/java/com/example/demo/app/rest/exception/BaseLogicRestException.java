package com.example.demo.app.rest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BaseLogicRestException extends RuntimeException{
    @Getter protected final HttpStatus status;
    public BaseLogicRestException(String message, HttpStatus status){
        super(message);
        this.status=status;
    }
}
