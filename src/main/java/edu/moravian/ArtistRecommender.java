package edu.moravian;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ArtistRecommender {

    private final Map<String, List<String>> artistsByMood = new HashMap<>();

    private final Random random = new Random();

    public ArtistRecommender() {
        if (!loadFromProjectRoot()) {
            loadFallback();
        }
        ensureMinimumNeutral();
    }


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
