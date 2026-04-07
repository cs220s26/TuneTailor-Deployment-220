package edu.moravian.exceptions;

public class InvalidCommandException extends RuntimeException {
    public InvalidCommandException(String cmd) {
        super("Unknown command: " + cmd);
    }
}
