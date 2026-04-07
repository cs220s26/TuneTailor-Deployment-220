package edu.moravian;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class SurveyGameTest {

    SurveyGame game;

    @BeforeEach
    void setup() {
        game = new SurveyGame(
                new MemorySurveyStorage(),
                new MoodAnalyzer(),
                new ArtistRecommender()
        );
    }

    @Test
    void soloStartsActive() {
        var q = game.startSolo("u1");
        assertNotNull(q);
        assertTrue(game.isSoloAnswerExpected("u1"));
    }

    @Test
    void soloAcceptsValidAnswer() {
        game.startSolo("u1");
        var r = game.submitSoloAnswer("u1", "wired");
        assertTrue(r.isValid());
    }

    @Test
    void soloRejectsInvalidAnswer() {
        game.startSolo("u1");
        var r = game.submitSoloAnswer("u1", "bad");
        assertFalse(r.isValid());
    }

    @Test
    void soloEventuallyFinishes() {
        game.startSolo("u1");
        game.submitSoloAnswer("u1", "wired");
        game.submitSoloAnswer("u1", "beat-heavy");
        game.submitSoloAnswer("u1", "sound");
        var r = game.submitSoloAnswer("u1", "party");
        assertNotNull(r.getSoloResult());
    }


    @Test
    void pairStartsAndJoins() {
        game.startPair("u1");
        var q = game.joinPair("u2");
        assertNotNull(q);
    }

    @Test
    void pairAcceptsFirstAnswer() {
        game.startPair("u1");
        game.joinPair("u2");
        var r = game.submitPairAnswer("u1", "wired");
        assertTrue(r.valid());
    }

    @Test
    void pairRejectsInvalidAnswer() {
        game.startPair("u1");
        game.joinPair("u2");
        var r = game.submitPairAnswer("u1", "bad");
        assertFalse(r.valid());
    }

    @Test
    void pairBlocksWrongUser() {
        game.startPair("u1");
        game.joinPair("u2");
        var r = game.submitPairAnswer("u2", "wired");
        assertNotNull(r.wrongTurnUser());
    }

    @Test
    void pairEventuallyFinishesRegardlessOfLength() {
        game.startPair("u1");
        game.joinPair("u2");

        String[] u1Answers = {"wired", "beat-heavy", "lyrics", "party"};
        String[] u2Answers = {"steady", "ambient", "sound", "chill"};

        int i = 0;
        boolean finished = false;

        // Safety cap prevents infinite loop if logic ever breaks
        while (!finished && i < 10) {
            var r1 = game.submitPairAnswer("u1", u1Answers[i % u1Answers.length]);
            if (r1.finished()) {
                finished = true;
                break;
            }

            var r2 = game.submitPairAnswer("u2", u2Answers[i % u2Answers.length]);
            if (r2.finished()) {
                finished = true;
                break;
            }

            i++;
        }

        assertTrue(finished, "Pair survey never reached finished state.");
    }
}
