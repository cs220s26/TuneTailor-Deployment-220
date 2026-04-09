package edu.moravian;

import java.util.HashMap;
import java.util.Map;

/**
 * Memory specific implementation of the storage interface
 *
 * Implements methods from the survey storage
 *
 * Stores user survey data in memory as a fallback for a Redis interruption
 */
public class MemorySurveyStorage implements SurveyStorage {

    // Solo survey state
    private final Map<String, Boolean> soloActive = new HashMap<>();
    private final Map<String, Boolean> soloPaused = new HashMap<>();
    private final Map<String, Integer> soloIndex = new HashMap<>();
    private final Map<String, Map<Integer, String>> soloAnswers = new HashMap<>();

    private boolean pairActive = false;
    private boolean pairPaused = false;
    private String pairUser1 = null;
    private String pairUser2 = null;

    // Current question index for the pair survey
    private int pairIndex = 0;

    // Tracks whose turn it is in the pair survey
    private int pairTurn = 1;

    // Stores answers for user 1 in the pair survey
    private final Map<Integer, String> pairAnswersUser1 = new HashMap<>();

    // Stores answers for user 2 in the pair survey
    private final Map<Integer, String> pairAnswersUser2 = new HashMap<>();

    @Override
    public boolean isSoloActive(String userId) {
        return soloActive.getOrDefault(userId, false);
    }

    @Override
    public boolean isSoloPaused(String userId) {
        return soloPaused.getOrDefault(userId, false);
    }

    @Override
    public void setSoloActive(String userId, boolean active) {
        if (active) {
            soloActive.put(userId, true);
        } else {
            soloActive.remove(userId);
        }
    }

    @Override
    public void setSoloPaused(String userId, boolean paused) {
        if (paused) {
            soloPaused.put(userId, true);
        } else {
            soloPaused.remove(userId);
        }
    }

    @Override
    public int getSoloIndex(String userId) {
        return soloIndex.getOrDefault(userId, 0);
    }

    @Override
    public void setSoloIndex(String userId, int index) {
        soloIndex.put(userId, index);
    }

    @Override
    public void saveSoloAnswer(String userId, int index, String answer) {
        soloAnswers.computeIfAbsent(userId, k -> new HashMap<>()).put(index, answer);
    }

    @Override
    public Map<Integer, String> getAllSoloAnswers(String userId) {
        return new HashMap<>(soloAnswers.getOrDefault(userId, new HashMap<>()));
    }

    @Override
    public void resetSolo(String userId) {
        soloActive.remove(userId);
        soloPaused.remove(userId);
        soloIndex.remove(userId);
    }

    @Override
    public void hardDeleteSolo(String userId) {
        soloActive.remove(userId);
        soloPaused.remove(userId);
        soloIndex.remove(userId);
        soloAnswers.remove(userId);
    }

    @Override
    public boolean isPairActive() {
        return pairActive;
    }

    @Override
    public boolean isPairPaused() {
        return pairPaused;
    }

    @Override
    public void setPairActive(boolean active) {
        this.pairActive = active;
    }

    @Override
    public void setPairPaused(boolean paused) {
        this.pairPaused = paused;
    }

    @Override
    public String getPairUser1() {
        return pairUser1;
    }

    @Override
    public String getPairUser2() {
        return pairUser2;
    }

    @Override
    public void setPairUser1(String userId) {
        this.pairUser1 = userId;
    }

    @Override
    public void setPairUser2(String userId) {
        this.pairUser2 = userId;
    }

    @Override
    public int getPairIndex() {
        return pairIndex;
    }

    @Override
    public void setPairIndex(int index) {
        this.pairIndex = index;
    }

    @Override
    public int getPairTurn() {
        return pairTurn;
    }

    @Override
    public void setPairTurn(int turn) {
        this.pairTurn = turn;
    }

    @Override
    public void savePairAnswerUser1(int index, String answer) {
        pairAnswersUser1.put(index, answer);
    }

    @Override
    public void savePairAnswerUser2(int index, String answer) {
        pairAnswersUser2.put(index, answer);
    }

    @Override
    public Map<Integer, String> getAllPairAnswersUser1() {
        return new HashMap<>(pairAnswersUser1);
    }

    @Override
    public Map<Integer, String> getAllPairAnswersUser2() {
        return new HashMap<>(pairAnswersUser2);
    }

    @Override
    public void resetPair() {
        pairActive = false;
        pairPaused = false;
        pairUser1 = null;
        pairUser2 = null;
        pairIndex = 0;
        pairTurn = 1;
    }

    @Override
    public void hardDeletePair() {
        pairActive = false;
        pairPaused = false;
        pairUser1 = null;
        pairUser2 = null;
        pairIndex = 0;
        pairTurn = 1;
        pairAnswersUser1.clear();
        pairAnswersUser2.clear();
    }
}