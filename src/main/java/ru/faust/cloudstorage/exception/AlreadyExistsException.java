package ru.faust.cloudstorage.exception;

import lombok.Getter;

@Getter
public class AlreadyExistsException extends RuntimeException {

    private final String errorType;

    public AlreadyExistsException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
    }
}
