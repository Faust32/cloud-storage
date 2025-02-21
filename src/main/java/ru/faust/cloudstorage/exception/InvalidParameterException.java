package ru.faust.cloudstorage.exception;

import lombok.Getter;

@Getter
public class InvalidParameterException extends RuntimeException {

    private final String errorType;

    public InvalidParameterException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
    }
}
