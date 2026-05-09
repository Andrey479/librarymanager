package com.andrey.librarymanager.exception;

public class BusinessException extends RuntimeException {
    public BusinessException (String message){
        super (message);
    }
}
