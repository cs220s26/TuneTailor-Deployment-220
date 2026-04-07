package edu.moravian;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class TuneTailorResponderTest {

    private TestBot bot;

    @BeforeEach
    void setup() {
        bot = new TestBot();
    }

    @Test
    void soloSurveyRunsToCompletion() {
        assertNotNull(bot.handle("u1", "!survey"));
        bot.handle("u1", "wired");
        bot.handle("u1", "beat-heavy");
        bot.handle("u1", "sound");
        String out = bot.handle("u1", "party");

        assertNotNull(out);
        assertFalse(out.isBlank());
    }

    @Test
    void pairSurveyRunsToCompletion() {
        bot.handle("u1", "!pairsurvey");
        bot.handle("u2", "!join");

        bot.handle("u1", "wired");
        bot.handle("u2", "steady");
        bot.handle("u1", "beat-heavy");
        bot.handle("u2", "slow");
        bot.handle("u1", "sound");
        bot.handle("u2", "lyrics");

        String out = bot.handle("u1", "party");

        assertNotNull(out);
        assertFalse(out.isBlank());
    }

    private static class TestBot {
        private final SurveyGame game;
        private final TuneTailorResponder responder;

        TestBot() {
            game = new SurveyGame(
                    new MemorySurveyStorage(),
                    new MoodAnalyzer(),
                    new ArtistRecommender()
            );
            responder = new TuneTailorResponder(game);
        }

        public String handle(String userId, String input) {
            return responder.handleTest(userId, input);
        }
    }
}
