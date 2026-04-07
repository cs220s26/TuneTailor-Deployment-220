package edu.moravian;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SurveyQuestionsTest {

    @Test
    void totalQuestionsIsFour() {
        assertEquals(4, SurveyQuestions.getTotalQuestions());
    }

    @Test
    void allowedResponseContainsKnownValue() {
        assertTrue(SurveyQuestions.getAllowedResponses(0).contains("wired"));
    }

    @Test
    void questionIsNotNull() {
        assertNotNull(SurveyQuestions.getQuestion(0));
    }

    @Test
    void allQuestionsAccessibleByIndex() {
        for (int i = 0; i < SurveyQuestions.getTotalQuestions(); i++) {
            assertNotNull(SurveyQuestions.getQuestion(i));
        }
    }

    @Test
    void allAllowedResponsesAccessibleByIndex() {
        for (int i = 0; i < SurveyQuestions.getTotalQuestions(); i++) {
            assertNotNull(SurveyQuestions.getAllowedResponses(i));
        }
    }

    @Test
    void allowedResponsesNeverEmpty() {
        for (int i = 0; i < SurveyQuestions.getTotalQuestions(); i++) {
            assertFalse(SurveyQuestions.getAllowedResponses(i).isEmpty());
        }
    }

    @Test
    void questionsAreUnique() {
        List<String> q1 = List.of(
                SurveyQuestions.getQuestion(0),
                SurveyQuestions.getQuestion(1),
                SurveyQuestions.getQuestion(2),
                SurveyQuestions.getQuestion(3)
        );

        assertEquals(q1.size(), q1.stream().distinct().count());
    }

    @Test
    void retrieveSoloAnswersReturnsCorrectOrder() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.saveSoloAnswer("u", 0, "wired");
        s.saveSoloAnswer("u", 1, "beat-heavy");
        s.saveSoloAnswer("u", 2, "sound");
        s.saveSoloAnswer("u", 3, "party");

        List<String> out = SurveyQuestions.retrieveAnswersInOrder(s, "u");

        assertEquals(List.of("wired", "beat-heavy", "sound", "party"), out);
    }

    @Test
    void retrieveSoloAnswersReturnsNullForUnanswered() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.saveSoloAnswer("u", 1, "beat-heavy");

        List<String> out = SurveyQuestions.retrieveAnswersInOrder(s, "u");

        assertNull(out.get(0));
        assertEquals("beat-heavy", out.get(1));
        assertNull(out.get(2));
        assertNull(out.get(3));
    }

    @Test
    void retrievePairAnswersUser1InOrder() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.savePairAnswerUser1(0, "wired");
        s.savePairAnswerUser1(1, "beat-heavy");
        s.savePairAnswerUser1(2, "sound");
        s.savePairAnswerUser1(3, "party");

        List<String> out = SurveyQuestions.retrieveAnswersInOrder(s, "u1", true);

        assertEquals(List.of("wired", "beat-heavy", "sound", "party"), out);
    }

    @Test
    void retrievePairAnswersUser2InOrder() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.savePairAnswerUser2(0, "steady");
        s.savePairAnswerUser2(1, "ambient");
        s.savePairAnswerUser2(2, "lyrics");
        s.savePairAnswerUser2(3, "chill");

        List<String> out = SurveyQuestions.retrieveAnswersInOrder(s, "u2", false);

        assertEquals(List.of("steady", "ambient", "lyrics", "chill"), out);
    }

    @Test
    void retrievePairAnswersUnansweredIndexesReturnNull() {
        MemorySurveyStorage s = new MemorySurveyStorage();
        s.savePairAnswerUser1(2, "sound");

        List<String> out = SurveyQuestions.retrieveAnswersInOrder(s, "u1", true);

        assertNull(out.get(0));
        assertNull(out.get(1));
        assertEquals("sound", out.get(2));
        assertNull(out.get(3));
    }

    @Test
    void allowedResponsesContainOnlyLowercase() {
        for (int i = 0; i < SurveyQuestions.getTotalQuestions(); i++) {
            for (String s : SurveyQuestions.getAllowedResponses(i)) {
                assertEquals(s.toLowerCase(), s);
            }
        }
    }

    @Test
    void totalQuestionsMatchesAllowedListSize() {
        assertEquals(
                SurveyQuestions.getTotalQuestions(),
                4
        );
    }

    @Test
    void questionTextIsNonEmpty() {
        for (int i = 0; i < SurveyQuestions.getTotalQuestions(); i++) {
            assertFalse(
                    SurveyQuestions.getQuestion(i).isBlank()
            );
        }
    }
}
