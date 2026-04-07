package edu.moravian;

import java.util.Map;

public interface SurveyStorage {

    boolean isSoloActive(String userId);

    boolean isSoloPaused(String userId);

    void setSoloActive(String userId, boolean active);

    void setSoloPaused(String userId, boolean paused);

    int getSoloIndex(String userId);

    void setSoloIndex(String userId, int index);

    void saveSoloAnswer(String userId, int index, String answer);

    // Retrieves all stored solo answers for a user
    Map<Integer, String> getAllSoloAnswers(String userId);

    // Clears all solo survey data for a user
    void resetSolo(String userId);

    boolean isPairActive();

    boolean isPairPaused();

    void setPairActive(boolean active);

    void setPairPaused(boolean paused);

    // Returns the first user in the pair survey
    String getPairUser1();

    // Returns the second user in the pair survey
    String getPairUser2();

    // Sets the first user in the pair survey
    void setPairUser1(String userId);

    // Sets the second user in the pair survey
    void setPairUser2(String userId);

    int getPairIndex();

    void setPairIndex(int index);

    int getPairTurn();

    void setPairTurn(int turn);

    void savePairAnswerUser1(int index, String answer);

    void savePairAnswerUser2(int index, String answer);

    // Retrieves all answers submitted by user 1 in the pair survey
    Map<Integer,String> getAllPairAnswersUser1();

    // Retrieves all answers submitted by user 2 in the pair survey
    Map<Integer,String> getAllPairAnswersUser2();

    // Clears all pair survey data
    void resetPair();
}
