package edu.moravian.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionsTests {


    @Test
    void surveyAlreadyActiveMessage() {
        assertTrue(new SurveyAlreadyActiveException("x")
                .getMessage().contains("already"));
    }

    @Test
    void surveyNotActiveMessage() {
        assertTrue(new SurveyNotActiveException("x")
                .getMessage().contains("not"));
    }

    @Test
    void playerNotFoundMessage() {
        assertTrue(new NoSuchPlayerException("x")
                .getMessage().contains("Player"));
    }

    @Test
    void invalidCommandMessage() {
        assertTrue(new InvalidCommandException("x")
                .getMessage().contains("Unknown"));
    }

    @Test
    void invalidResponseMessage() {
        assertEquals("x",
                new InvalidResponseException("x").getMessage());
    }

    @Test
    void storageExceptionStoresCause() {
        RuntimeException inner = new RuntimeException();
        StorageException ex = new StorageException("x", inner);
        assertEquals(inner, ex.getCause());
    }

    @Test
    void conflictExactMessage() {
        assertEquals("bad",
                new PairModeConflictException("bad").getMessage());
    }

    @Test
    void exceptionsAreRuntime() {
        assertTrue(new SurveyNotActiveException("x") instanceof RuntimeException);
    }

    @Test
    void storageExceptionWithoutCauseHasNullCause() {
        StorageException ex = new StorageException("x");
        assertNull(ex.getCause());
    }

    @Test
    void surveyAlreadyActiveFullMessageFormat() {
        String msg = new SurveyAlreadyActiveException("test").getMessage();
        assertTrue(msg.startsWith("Survey already active"));
    }

    @Test
    void surveyNotActiveFullMessageFormat() {
        String msg = new SurveyNotActiveException("test").getMessage();
        assertTrue(msg.startsWith("Survey not active"));
    }

    @Test
    void noSuchPlayerIncludesId() {
        String msg = new NoSuchPlayerException("abc").getMessage();
        assertTrue(msg.contains("abc"));
    }

    @Test
    void invalidCommandIncludesCommand() {
        String msg = new InvalidCommandException("!bad").getMessage();
        assertTrue(msg.contains("!bad"));
    }

    @Test
    void storageExceptionInheritsFromRuntime() {
        StorageException ex = new StorageException("x");
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void pairModeConflictIsRuntime() {
        assertTrue(new PairModeConflictException("x")
                instanceof RuntimeException);
    }

    @Test
    void invalidResponseIsRuntime() {
        assertTrue(new InvalidResponseException("x")
                instanceof RuntimeException);
    }

    @Test
    void allExceptionsAreThrowable() {
        assertTrue(new SurveyAlreadyActiveException("x") instanceof Throwable);
        assertTrue(new SurveyNotActiveException("x") instanceof Throwable);
        assertTrue(new NoSuchPlayerException("x") instanceof Throwable);
        assertTrue(new InvalidCommandException("x") instanceof Throwable);
        assertTrue(new InvalidResponseException("x") instanceof Throwable);
        assertTrue(new PairModeConflictException("x") instanceof Throwable);
        assertTrue(new StorageException("x") instanceof Throwable);
    }
}
