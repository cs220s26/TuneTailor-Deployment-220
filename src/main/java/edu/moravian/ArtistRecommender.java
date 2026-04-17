package edu.moravian;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Reccomends artists based on different moods associated with an artist from
 * a file names artists.txt
 */
public class ArtistRecommender {

    private final Map<String, List<String>> artistsByMood = new HashMap<>();

    private final Random random = new Random();

    /**
     * Runs the components to recommend artists based on mood
     */
    public ArtistRecommender() {
        if (!loadFromProjectRoot()) {
            loadFallback();
        }
        ensureMinimumNeutral();
    }

    /**
     * Loads contents from artists.txt
     * @return
     */
    private boolean loadFromProjectRoot() {
        try {
            Path path = Path.of("artists.txt");
            if (!Files.exists(path)) return false;

            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                if (line.isBlank() || !line.contains(":")) continue;

                String[] parts = line.split(":", 2);
                String mood = parts[0].trim().toLowerCase();
                String artist = parts[1].trim();

                artistsByMood
                        .computeIfAbsent(mood, k -> new ArrayList<>())
                        .add(artist);
            }

            return !artistsByMood.isEmpty();

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reccomends artists based on a mood.  If the mood is not set, it is set to neutral
     * @param mood - mood of user
     * @return - random choice of matching artists
     */
    public List<String> recommendSolo(String mood) {
        if (mood == null) mood = "neutral";

        List<String> pool = artistsByMood.getOrDefault(
                mood.toLowerCase(),
                artistsByMood.get("neutral")
        );

        if (pool == null || pool.isEmpty())
            return pickRandom(artistsByMood.get("neutral"), 3);

        return pickRandom(pool, 3);
    }

    /**
     * Recommends artists based on a mood for both users.  Sets mood to neutral if no mood is present
     * @param mood1 - mood of first user
     * @param mood2 - mood of second user
     * @return - random choice of matching artists
     */
    public List<String> recommendPair(String mood1, String mood2) {

        if (Objects.equals(mood1, mood2))
            return recommendSolo(mood1);

        Set<String> combined = new LinkedHashSet<>();

        if (mood1 != null && artistsByMood.containsKey(mood1))
            combined.addAll(artistsByMood.get(mood1));

        if (mood2 != null && artistsByMood.containsKey(mood2))
            combined.addAll(artistsByMood.get(mood2));

        if (combined.isEmpty())
            combined.addAll(artistsByMood.get("neutral"));

        return pickRandom(new ArrayList<>(combined), 3);
    }

    /**
     * Picks random artists from the pool based on mood
     * @param pool - pool of artists from the file
     * @param amount - number of artists in pool
     * @return list of artists
     */
    private List<String> pickRandom(List<String> pool, int amount) {
        List<String> copy = new ArrayList<>(pool);
        Collections.shuffle(copy, random);

        return copy.stream()
                .limit(Math.min(amount, copy.size()))
                .toList();
    }

    // Ensures the neutral mood always has at least three artists
    private void ensureMinimumNeutral() {
        artistsByMood.putIfAbsent("neutral", new ArrayList<>());

        List<String> neutral = artistsByMood.get("neutral");

        while (neutral.size() < 3) {
            neutral.add("Neutral Artist " + (neutral.size() + 1));
        }
    }

    // Loads default artist values if the file cannot be read (preset artists)
    private void loadFallback() {
        artistsByMood.put("happy", new ArrayList<>(List.of("Dua Lipa", "Bruno Mars", "Pharrell Williams")));
        artistsByMood.put("sad", new ArrayList<>(List.of("Radiohead", "Halsey", "Billie Eilish")));
        artistsByMood.put("calm", new ArrayList<>(List.of("Clairo", "Bon Iver", "Joji")));
        artistsByMood.put("energetic", new ArrayList<>(List.of("Skrillex", "Doja Cat", "The Weeknd")));
        artistsByMood.put("neutral", new ArrayList<>(List.of("Taylor Swift", "Coldplay", "AURORA")));
    }
}
