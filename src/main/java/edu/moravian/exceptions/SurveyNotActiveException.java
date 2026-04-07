package edu.moravian.exceptions;

public class SurveyNotActiveException extends RuntimeException {
    public SurveyNotActiveException(String msg) { super("Survey not active: " + msg); }
}