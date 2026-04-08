package edu.moravian;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MemorySurveyStorageTest {

    @Test
    public void testSoloFlow() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setSoloActive("u1", true);
        s.setSoloIndex("u1", 1);
        s.saveSoloAnswer("u1", 1, "happy");

        assertTrue(s.isSoloActive("u1"));
        assertEquals(1, s.getSoloIndex("u1"));
        assertEquals(Map.of(1, "happy"), s.getAllSoloAnswers("u1"));
    }

    @Test
    public void testResetSolo() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setSoloActive("u1", true);
        s.saveSoloAnswer("u1", 0, "happy");
        s.resetSolo("u1");

        assertFalse(s.isSoloActive("u1"));
        assertFalse(s.getAllSoloAnswers("u1").isEmpty());
    }

    @Test
    public void testPairFlow() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setPairActive(true);
        s.setPairUser1("u1");
        s.setPairUser2("u2");
        s.setPairTurn(2);

        assertTrue(s.isPairActive());
        assertEquals("u1", s.getPairUser1());
        assertEquals(2, s.getPairTurn());
    }

    @Test
    public void testResetPair() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setPairActive(true);
        s.resetPair();

        assertFalse(s.isPairActive());
        assertNull(s.getPairUser1());
    }

    @Test void soloIndexDefaultsZero() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        assertEquals(0, s.getSoloIndex("u"));
    }

    @Test void soloPauseDefaultsFalse() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        assertFalse(s.isSoloPaused("u"));
    }

    @Test void soloPauseAfterSet() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setSoloPaused("u", true);
        assertTrue(s.isSoloPaused("u"));
    }

    @Test void soloMultipleAnswersStored() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.saveSoloAnswer("u",0,"happy");
        s.saveSoloAnswer("u",1,"calm");
        assertEquals(2, s.getAllSoloAnswers("u").size());
    }

    @Test void pairTurnDefaultsOne() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        assertEquals(1, s.getPairTurn());
    }

    @Test void pairResetResetsTurn() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setPairTurn(2);
        s.resetPair();
        assertEquals(1, s.getPairTurn());
    }

    @Test void soloPausedIndependentPerUser() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setSoloPaused("u1", true);
        assertFalse(s.isSoloPaused("u2"));
    }

    @Test void soloAnswersSeparateByUser() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.saveSoloAnswer("u1",0,"happy");
        s.saveSoloAnswer("u2",0,"sad");
        assertNotEquals(
                s.getAllSoloAnswers("u1").get(0),
                s.getAllSoloAnswers("u2").get(0)
        );
    }

    @Test void pairUser2ClearedOnReset() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setPairUser2("u2");
        s.resetPair();
        assertNull(s.getPairUser2());
    }

    @Test void pairPausedClearsOnReset() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setPairPaused(true);
        s.resetPair();
        assertFalse(s.isPairPaused());
    }

    @Test
    void soloInactiveByDefault() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        assertFalse(s.isSoloActive("x"));
    }

    @Test
    void soloActiveAfterSetTrue() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setSoloActive("x", true);
        assertTrue(s.isSoloActive("x"));
    }

    @Test
    void soloActiveFalseAfterSetFalse() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setSoloActive("x", true);
        s.setSoloActive("x", false);
        assertFalse(s.isSoloActive("x"));
    }

    @Test
    void soloPausedFalseAfterReset() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setSoloPaused("x", true);
        s.resetSolo("x");
        assertFalse(s.isSoloPaused("x"));
    }

    @Test
    void soloIndexOverwrittenCorrectly() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setSoloIndex("x", 1);
        s.setSoloIndex("x", 5);
        assertEquals(5, s.getSoloIndex("x"));
    }

    @Test
    void soloAnswersOverwriteSameIndex() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.saveSoloAnswer("x",0,"happy");
        s.saveSoloAnswer("x",0,"sad");
        assertEquals("sad", s.getAllSoloAnswers("x").get(0));
    }

    @Test
    void soloResetClearsIndex() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setSoloIndex("x", 3);
        s.resetSolo("x");
        assertEquals(0, s.getSoloIndex("x"));
    }

    @Test
    void pairInactiveByDefault() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        assertFalse(s.isPairActive());
    }

    @Test
    void pairActiveAfterSet() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setPairActive(true);
        assertTrue(s.isPairActive());
    }

    @Test
    void pairPausedFalseByDefault() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        assertFalse(s.isPairPaused());
    }

    @Test
    void pairPausedTrueAfterSet() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setPairPaused(true);
        assertTrue(s.isPairPaused());
    }

    @Test
    void pairUser1SetAndGet() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setPairUser1("A");
        assertEquals("A", s.getPairUser1());
    }

    @Test
    void pairUser2SetAndGet() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setPairUser2("B");
        assertEquals("B", s.getPairUser2());
    }

    @Test
    void pairAnswersUser1Stored() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.savePairAnswerUser1(0,"x");
        assertEquals("x", s.getAllPairAnswersUser1().get(0));
    }

    @Test
    void pairAnswersUser2Stored() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.savePairAnswerUser2(0,"y");
        assertEquals("y", s.getAllPairAnswersUser2().get(0));
    }

    @Test
    void pairResetClearsAnswers() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.savePairAnswerUser1(0,"x");
        s.resetPair();
        assertFalse(s.getAllPairAnswersUser1().isEmpty());
    }

    @Test
    void pairIndexDefaultsZero() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        assertEquals(0, s.getPairIndex());
    }

    @Test
    void pairIndexAfterSet() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.setPairIndex(4);
        assertEquals(4, s.getPairIndex());
    }

    @Test
    void hardDeleteSoloClearsAnswers() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.saveSoloAnswer("u1", 0, "happy");

        s.hardDeleteSolo("u1");

        assertTrue(s.getAllSoloAnswers("u1").isEmpty());
    }

    @Test
    void hardDeletePairClearsAnswers() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.savePairAnswerUser1(0, "x");
        s.savePairAnswerUser2(0, "y");

        s.hardDeletePair();

        assertTrue(s.getAllPairAnswersUser1().isEmpty());
        assertTrue(s.getAllPairAnswersUser2().isEmpty());
    }
}