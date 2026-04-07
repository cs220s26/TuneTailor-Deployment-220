package edu.moravian;

import java.util.*;

public class MoodAnalyzer {

    public String determineMood(List<String> answers) {

        // Default to neutral if no answers are provided
        if (answers == null || answers.isEmpty())
            return "neutral";

        // Stores the score for each possible mood
        Map<String, Integer> score = new LinkedHashMap<>();
        score.put("happy", 0);
        score.put("sad", 0);
        score.put("calm", 0);
        score.put("energetic", 0);

        // Scores each answer based on predefined mappings
        for (String a : answers) {
            if (a == null) continue;

            switch (a.toLowerCase()) {

                case "wired"      -> score.merge("energetic", 1, Integer::sum);
                case "steady"     -> score.merge("calm", 1, Integer::sum);
                case "drained"    -> score.merge("sad", 1, Integer::sum);
                case "relaxed"    -> score.merge("happy", 1, Integer::sum);

                case "beat-heavy" -> score.merge("energetic", 1, Integer::sum);
                case "ambient"    -> score.merge("calm", 1, Integer::sum);
                case "slow"       -> score.merge("sad", 1, Integer::sum);
                case "uplifting"  -> score.merge("happy", 1, Integer::sum);

                case "lyrics"     -> score.merge("sad", 1, Integer::sum);
                case "sound"      -> score.merge("energetic", 1, Integer::sum);

                case "party"      -> score.merge("energetic", 1, Integer::sum);
                case "chill"      -> score.merge("calm", 1, Integer::sum);
                case "alone"      -> score.merge("sad", 1, Integer::sum);
                case "anytime"    -> score.merge("happy", 1, Integer::sum);
            }
        }

        // Returns neutral if no mood was affected by the answers
        boolean allZero = score.values().stream().allMatch(v -> v == 0);
        if (allZero) return "neutral";

        // Returns the mood with the highest score
        return score.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }
}
