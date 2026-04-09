package edu.moravian;

import java.util.Map;

/**
 * Interface defining how to handle user data communicated to the bot
 * Need to come back to this later
 */
public interface SurveyStorage {

    /**
     * Determines if a solo survey is currently active
     * @param userId - User's ID
     * @return boolean
     */
    boolean isSoloActive(String userId);

    /**
     * Determines if a solo survey is paused or not
     * @param userId - User's ID
     * @return boolean
     */
    boolean isSoloPaused(String userId);

    /**
     * Sets the condition that a solo survey is active
     * @param userId - User's ID
     * @param active - boolean (active or inactive)
     */
    void setSoloActive(String userId, boolean active);

    /**
     * Sets the condition that a solo survey is paused
     * @param userId - User's ID
     * @param paused - boolean (paused or not paused)
     */
    void setSoloPaused(String userId, boolean paused);

    /**
     * Gets the index where the information is being stored with the user
     * @param userId - User's ID
     * @return - index with user's ID
     */
    int getSoloIndex(String userId);

    /**
     * Sets the index for a user's solo survey
     * @param userId - User's ID
     * @param index - index with user's ID
     */
    void setSoloIndex(String userId, int index);

    /**
     * Saves the answer for the respective user in a database
     * @param userId - User's ID
     * @param index - the index at
     * @param answer - answer based off of user's survey data
     */
    void saveSoloAnswer(String userId, int index, String answer);


    /**
     * Retrieves all stored solo answers for a user
     * @param userId - User ID
     * @return The answers for the user's solo survey
     */
    Map<Integer, String> getAllSoloAnswers(String userId);

    /**
     * Clears solo session state but preserves answers
     * @param userId - User's ID
     */
    void resetSolo(String userId);

    /**
     * Hard deletes all solo survey data including answers
     * @param userId - User's ID
     */
    void hardDeleteSolo(String userId);

    /**
     * Returns true or false whether or not a pair survey is active
     * @return boolean
     */
    boolean isPairActive();

    /**
     * Returns true or false whether or not a pair survey is paused
     * @return boolean
     */
    boolean isPairPaused();

    /**
     * Changes the pair survey to an active state
     * @param active - whether or not survey is active
     */
    void setPairActive(boolean active);

    /**
     * Chnages the pair survey to a paused state
     * @param paused - whether or not survey is active
     */
    void setPairPaused(boolean paused);

    /**
     * Returns the first user in the pair survey
     * @return first user in survey pair
     */
    String getPairUser1();

    /**
     * Returns the second user in the pair survey
     * @return second user in survey pair
     */
    String getPairUser2();

    /**
     * Sets the first user in the pair survey
     * @param userId - UserID
     */
    void setPairUser1(String userId);

    /**
     * Sets the second user in the pair survey
     * @param userId - UserID
     */
    void setPairUser2(String userId);

    /**
     * Gets the index where the pair survey data is being stored
     * @return - pair survey index
     */
    int getPairIndex();

    /**
     * Sets the index where the pair survey data is being stored
     * @param index - pair survey index
     */
    void setPairIndex(int index);

    /**
     * Gets the user who currently has a turn
     * @return user with turn
     */
    int getPairTurn();

    /**
     * Sets the turn of the next user
     * @param turn - which user should have the turn next
     */
    void setPairTurn(int turn);

    /**
     * Saves user 1's answer in the pair survey
     * @param index - user 1's index
     * @param answer - user 1's answers
     */
    void savePairAnswerUser1(int index, String answer);

    /**
     * Saves user 2's answer in the pair survey
     * @param index - user 2's index
     * @param answer - user 2's answers
     */
    void savePairAnswerUser2(int index, String answer);

    /**
     * Retrieves all answers submitted by user 1 in the pair survey
     * @return user 1's answers
     */
    Map<Integer, String> getAllPairAnswersUser1();

    /**
     * Retrieves all answers submitted by user 2 in the pair survey
     * @return user 2's answers
     */
    Map<Integer, String> getAllPairAnswersUser2();

    /**
     * Clears pair session state but preserves answers
     */
    void resetPair();

    /**
     * Hard deletes all pair survey data including answers
     */
    void hardDeletePair();
}