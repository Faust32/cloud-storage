package ru.faust.cloudstorage.exception;

public class NoSuchFileException extends RuntimeException {
    public NoSuchFileException(String message) {
        super(message);
    }
}
