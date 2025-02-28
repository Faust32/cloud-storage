package ru.faust.cloudstorage.exception;

public class CreateBucketException extends RuntimeException {
    public CreateBucketException(String message) {
        super(message);
    }
}
