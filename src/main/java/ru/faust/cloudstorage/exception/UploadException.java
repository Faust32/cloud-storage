package ru.faust.cloudstorage.exception;

public class UploadException extends RuntimeException {
    public UploadException(String message) {
        super(message);
    }
}
