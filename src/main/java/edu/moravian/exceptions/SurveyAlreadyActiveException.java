package edu.moravian.exceptions;

public class SurveyAlreadyActiveException extends RuntimeException {
    public SurveyAlreadyActiveException(String msg) { super("Survey already active: " + msg); }
}