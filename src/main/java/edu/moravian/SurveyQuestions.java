package edu.moravian;

import java.util.*;

/**
 * 
 */
public class SurveyQuestions {

    // Stores all survey questions in order
    private static final List<String> QUESTIONS = List.of(
            "What best describes your energy right now?",
            "What kind of sound are you drawn to?",
            "What matters more to you right now?",
            "When would you most likely listen?"
    );

    // Stores the allowed responses for each question
    private static final List<List<String>> ALLOWED = List.of(
            List.of("wired", "steady", "drained", "relaxed"),
            List.of("beat-heavy", "ambient", "slow", "uplifting"),
            List.of("lyrics", "sound"),
            List.of("party", "chill", "alone", "anytime")
    );

    public static String getQuestion(int index) {
        return QUESTIONS.get(index);
    }

    public static List<String> getAllowedResponses(int index) {
        return ALLOWED.get(index);
    }

    public static int getTotalQuestions() {
        return QUESTIONS.size();
    }

    // Retrieves solo answers in question order
    public static List<String> retrieveAnswersInOrder(SurveyStorage storage, String userId) {
        Map<Integer,String> map = storage.getAllSoloAnswers(userId);
        List<String> out = new ArrayList<>();

        for (int i = 0; i < getTotalQuestions(); i++)
            out.add(map.get(i));

        return out;
    }

    // Retrieves pair answers in question order
    public static List<String> retrieveAnswersInOrder(
            SurveyStorage storage,
            String userId,
            boolean first
    ) {
        Map<Integer,String> map =
                first ? storage.getAllPairAnswersUser1()
                        : storage.getAllPairAnswersUser2();

        List<String> out = new ArrayList<>();

        for (int i = 0; i < getTotalQuestions(); i++)
            out.add(map.get(i));

        return out;
    }
}
