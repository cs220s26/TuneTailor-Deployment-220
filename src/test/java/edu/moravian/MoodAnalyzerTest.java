package edu.moravian;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoodAnalyzerTest {

    private final MoodAnalyzer analyzer = new MoodAnalyzer();

    @Test
    void energeticWins() {
        assertEquals("energetic",
                analyzer.determineMood(List.of("wired", "beat-heavy")));
    }

    @Test
    void calmWins() {
        assertEquals("calm",
                analyzer.determineMood(List.of("steady", "ambient", "chill")));
    }

    @Test
    void sadWins() {
        assertEquals("sad",
                analyzer.determineMood(List.of("drained", "slow", "alone")));
    }

    @Test
    void happyWins() {
        assertEquals("happy",
                analyzer.determineMood(List.of("relaxed", "uplifting", "anytime")));
    }

    @Test
    void ignoresInvalid() {
        List<String> input = new ArrayList<>();
        input.add("relaxed");
        input.add("INVALID");   // ignored
        input.add(null);        // ignored

        assertEquals("happy", analyzer.determineMood(input));
    }

    @Test
    void emptyReturnsNeutral() {
        assertEquals("neutral", analyzer.determineMood(List.of()));
    }

    @Test
    void nullReturnsNeutral() {
        assertEquals("neutral", analyzer.determineMood(null));
    }

    @Test
    void onlyInvalidValuesReturnsNeutral() {
        assertEquals("neutral",
                analyzer.determineMood(List.of("bad", "wrong", "nope")));
    }

    @Test
    void mixedValidAndInvalidStillCountsValid() {
        assertEquals("energetic",
                analyzer.determineMood(List.of("wired", "bad", "wrong")));
    }

    @Test
    void nullElementsAreSafelyIgnored() {
        List<String> input = new ArrayList<>();
        input.add(null);
        input.add("relaxed");
        input.add(null);

        assertEquals("happy", analyzer.determineMood(input));
    }

    @Test
    void capitalizationDoesNotAffectDetection() {
        assertEquals("energetic",
                analyzer.determineMood(List.of("WiReD", "BEAT-heavy")));
    }

    @Test
    void tieBreakerUsesInsertionOrderEnergeticBeforeHappy() {
        // energetic = 1 (wi
    }
}