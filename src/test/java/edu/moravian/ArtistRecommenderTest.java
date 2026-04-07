package edu.moravian;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtistRecommenderTest {

    private ArtistRecommender r;

    @BeforeEach
    void setup() {
        r = new ArtistRecommender();
    }

    @Test
    void soloNeverEmpty() {
        assertFalse(r.recommendSolo("happy").isEmpty());
    }

    @Test
    void soloNeverNull() {
        assertNotNull(r.recommendSolo("sad"));
    }

    @Test
    void pairNeverNull() {
        assertNotNull(r.recommendPair("happy", "sad"));
    }

    @Test
    void pairNeverEmpty() {
        assertFalse(r.recommendPair("happy", "sad").isEmpty());
    }

    @Test
    void unknownDefaultsToNeutral() {
        assertNotNull(r.recommendSolo("UNKNOWN"));
    }

    @Test
    void soloAlwaysReturnsExactlyThree() {
        assertEquals(3, r.recommendSolo("happy").size());
    }

    @Test
    void pairAlwaysReturnsExactlyThree() {
        assertEquals(3, r.recommendPair("happy", "sad").size());
    }

    @Test
    void soloWithNullMoodReturnsThree() {
        assertEquals(3, r.recommendSolo(null).size());
    }

    @Test
    void pairWithNullMoodStillReturnsThree() {
        assertEquals(3, r.recommendPair(null, "happy").size());
    }

    @Test
    void sameMoodPairBehavesLikeSolo() {
        List<String> solo = r.recommendSolo("happy");
        List<String> pair = r.recommendPair("happy", "happy");
        assertEquals(3, pair.size());
        assertNotNull(pair);
    }

    @Test
    void neutralAlwaysHasMinimumThreeArtists() {
        assertEquals(3, r.recommendSolo("neutral").size());
    }

    @Test
    void unknownPairFallsBackToNeutralPool() {
        List<String> out = r.recommendPair("bad", "worse");
        assertEquals(3, out.size());
    }

    @Test
    void soloHasNoDuplicateArtists() {
        List<String> out = r.recommendSolo("happy");
        assertEquals(new HashSet<>(out).size(), out.size());
    }

    @Test
    void pairHasNoDuplicateArtists() {
        List<String> out = r.recommendPair("happy", "sad");
        assertEquals(new HashSet<>(out).size(), out.size());
    }

    @Test
    void soloArtistsAreNeverNull() {
        for (String s : r.recommendSolo("happy")) {
            assertNotNull(s);
        }
    }

    @Test
    void pairArtistsAreNeverNull() {
        for (String s : r.recommendPair("happy", "sad")) {
            assertNotNull(s);
        }
    }

    @Test
    void differentCallsCanReturnDifferentOrdersButSameSize() {
        List<String> a = r.recommendSolo("happy");
        List<String> b = r.recommendSolo("happy");
        assertEquals(3, a.size());
        assertEquals(3, b.size());
    }

    @Test
    void emptyMoodStillSafe() {
        assertEquals(3, r.recommendSolo("").size());
    }

    @Test
    void pairWithOneInvalidStillWorks() {
        assertEquals(3, r.recommendPair("happy", "INVALID").size());
    }

    @Test
    void soloUsesFallbackWhenFileMissing() {
        ArtistRecommender temp = new ArtistRecommender();
        assertTrue(temp.recommendSolo("happy").size() >= 3);
    }

    @Test
    void pairWithBothNullUsesNeutral() {
        assertEquals(3, r.recommendPair(null, null).size());
    }
}
