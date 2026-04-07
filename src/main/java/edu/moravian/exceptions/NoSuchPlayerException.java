
package edu.moravian.exceptions;

public class NoSuchPlayerException extends RuntimeException {
    public NoSuchPlayerException(String msg) { super("Player not part of the survey: " + msg); }
}