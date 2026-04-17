package edu.moravian;

import java.util.List;

/**
 * Holds responses to be sent out to participants when interacting with the bot
 * These methods are used by the TuneTailorResponder class when a specific message
 * needs to be displayed to the user
 */
public class TuneTailorResponses {

    public static String help() {
        return """
                🎧 **TuneTailor Commands**
                **!help** - pulls this menu up
                **!survey** – start a solo survey
                **!pairsurvey** – host a pair survey
                **!join** – join the pair survey
                **!pause solo / !resume solo**
                **!pause pair / !resume pair**
                **!stop** – stop surveys but preserve stored answers
                """;

    }

    public static String stopped() {
        return "🛑 All surveys stopped. Stored answers were preserved.";
    }

    public static String unknown() {
        return "❓ I didn't understand that. Type `!help`.";
    }

    public static String noPaused() {
        return "⚠ No paused survey found.";
    }

    public static String soloStart(SurveyGame.Question q) {
        return """
                🟢 **SOLO SURVEY |**

                You're up!

                Q%d: %s
                Options: %s
                """.formatted(
                q.getIndex() + 1,
                q.getText(),
                q.getAllowed()
        );
    }

    public static String nextSolo(SurveyGame.Question q) {
        return """
                🟢 **SOLO SURVEY |**

                You're up!

                Q%d: %s
                Options: %s
                """.formatted(
                q.getIndex() + 1,
                q.getText(),
                q.getAllowed()
        );
    }

    public static String invalidSolo(SurveyGame.AnswerResult r) {
        List<String> allowed = r.getAllowed();
        return "❌ Invalid answer. Allowed: " + allowed;
    }

    public static String soloPaused() {
        return "⏸ Solo survey paused.";
    }

    public static String soloResumed(SurveyGame.Question q) {
        return "▶ **Resumed Solo Survey!**\n" + nextSolo(q);
    }

    public static String soloFinished(SurveyGame.SoloResult r) {
        return """
                🎉 **SOLO SURVEY COMPLETE!**

                **Detected Mood:** %s
                **Recommended Artists:** %s
                """.formatted(r.getMood(), r.getArtists());
    }

    public static String soloBlockedByPair() {
        return "🚫 Cannot start solo — pair survey already active.";
    }

    public static String pairLobby(String hostId) {
        return """
                🟣 **PAIR SURVEY |**
                Pair formed!
                Waiting for second participant.

                Type **!join** to participate.
                """;
    }

    public static String pairStart(SurveyGame.Question q, String u1, String u2) {
        return """
            🟣 **PAIR SURVEY |**

            You're up first!

            Q%d: %s
            Options: %s
            """.formatted(
                q.getIndex() + 1,
                q.getText(),
                q.getAllowed()
        );
    }

    public static String nextPair(SurveyGame.Question q, String userTurn) {
        return """
                🟣 **PAIR SURVEY |**

                You're up!

                Q%d: %s
                Options: %s
                """.formatted(
                q.getIndex() + 1,
                q.getText(),
                q.getAllowed()
        );
    }

    public static String invalidPair(SurveyGame.AnswerResultPair r) {
        return "❌ Invalid answer. Allowed: " + r.allowed();
    }

    public static String pairWrongUser(String user) {
        return """
                🟣 **PAIR SURVEY |**

                ⏳ It's not your turn yet!
                """;
    }

    public static String pairPaused() {
        return "⏸ Pair survey paused.";
    }

    public static String pairResumed(SurveyGame.Question q) {
        return "▶ **Resumed Pair Survey!**\n" + nextPair(q, "");
    }

    public static String pairFinished(SurveyGame.PairResult r, String u1, String u2) {
        return """
                🎉 **PAIR SURVEY COMPLETE!**

                **User 1 Mood:** %s
                **User 2 Mood:** %s
                **Recommended Artists:** %s
                """.formatted(
                r.getMood1(),
                r.getMood2(),
                r.getArtists()
        );
    }
}
