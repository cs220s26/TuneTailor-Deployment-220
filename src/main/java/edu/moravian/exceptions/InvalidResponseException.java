package edu.moravian.exceptions;

public class InvalidResponseException extends RuntimeException {
    public InvalidResponseException(String msg) {
        super(msg);
    }
}
