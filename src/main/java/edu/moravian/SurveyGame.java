package edu.moravian;

import java.util.List;

public class SurveyGame {

    private final SurveyStorage storage;

    private final MoodAnalyzer analyzer;

    private final ArtistRecommender recommender;

    public SurveyGame(SurveyStorage storage,
                      MoodAnalyzer analyzer,
                      ArtistRecommender recommender) {
        this.storage = storage;
        this.analyzer = analyzer;
        this.recommender = recommender;
    }


    public static class Question {
        private final String text;
        private final List<String> allowed;
        private final int index;
        private final String nextUser;

        public Question(String text, List<String> allowed, int index, String nextUser) {
            this.text = text;
            this.allowed = allowed;
            this.index = index;
            this.nextUser = nextUser;
        }

        public String getText() { return text; }
        public List<String> getAllowed() { return allowed; }
        public int getIndex() { return index; }
        public String getNextUser() { return nextUser; }
    }


    public static class SoloResult {
        private final String mood;
        private final List<String> artists;

        public SoloResult(String mood, List<String> artists) {
            this.mood = mood;
            this.artists = artists;
        }

        public String getMood() { return mood; }
        public List<String> getArtists() { return artists; }
    }


    public static class PairResult {
        private final String mood1;
        private final String mood2;
        private final List<String> artists;

        public PairResult(String mood1, String mood2, List<String> artists) {
            this.mood1 = mood1;
            this.mood2 = mood2;
            this.artists = artists;
        }

        public String getMood1() { return mood1; }
        public String getMood2() { return mood2; }
        public List<String> getArtists() { return artists; }
    }


    public static class AnswerResult {
        private final boolean valid;
        private final boolean finished;
        private final List<String> allowed;
        private final Question nextQuestion;
        private final SoloResult soloResult;

        public AnswerResult(boolean valid, boolean finished,
                            List<String> allowed,
                            Question nextQuestion,
                            SoloResult soloResult) {
            this.valid = valid;
            this.finished = finished;
            this.allowed = allowed;
            this.nextQuestion = nextQuestion;
            this.soloResult = soloResult;
        }

        public boolean isValid() { return valid; }
        public boolean isFinished() { return finished; }
        public List<String> getAllowed() { return allowed; }
        public Question getNextQuestion() { return nextQuestion; }
        public SoloResult getSoloResult() { return soloResult; }
    }


    public static class AnswerResultPair {

        private final boolean valid;
        private final boolean finished;
        private final List<String> allowed;
        private final String wrongTurnUser;
        private final Question nextQuestion;
        private final PairResult result;
        private final String nextUser;
        private final String user1;
        private final String user2;

        public AnswerResultPair(boolean v, boolean f, List<String> a,
                                String wrong, Question q, PairResult r,
                                String next, String u1, String u2) {
            valid = v;
            finished = f;
            allowed = a;
            wrongTurnUser = wrong;
            nextQuestion = q;
            result = r;
            nextUser = next;
            user1 = u1;
            user2 = u2;
        }

        public boolean valid() { return valid; }
        public boolean finished() { return finished; }
        public List<String> allowed() { return allowed; }
        public String wrongTurnUser() { return wrongTurnUser; }
        public PairResult result() { return result; }
        public Question nextQuestion() { return nextQuestion; }
        public String nextUser() { return nextUser; }
        public String user1() { return user1; }
        public String user2() { return user2; }
    }


    public boolean isSoloAnswerExpected(String id) {
        return storage.isSoloActive(id) && !storage.isSoloPaused(id);
    }


    public boolean isPairAnswerExpected(String id) {
        return storage.isPairActive() && !storage.isPairPaused() &&
                (id.equals(storage.getPairUser1()) ||
                        id.equals(storage.getPairUser2()));
    }

    public void forceStopAllSurveys(String id) {
        try { storage.resetSolo(id); } catch (Exception ignored) {}
        try { storage.resetPair(); } catch (Exception ignored) {}
    }

    public Question startSolo(String id) {
        storage.setSoloActive(id, true);
        storage.setSoloPaused(id, false);
        storage.setSoloIndex(id, 0);

        return new Question(
                SurveyQuestions.getQuestion(0),
                SurveyQuestions.getAllowedResponses(0),
                0, id
        );
    }

    public AnswerResult submitSoloAnswer(String id, String msg) {
        int index = storage.getSoloIndex(id);
        List<String> allowed = SurveyQuestions.getAllowedResponses(index);

        if (!allowed.contains(msg))
            return new AnswerResult(false,false,allowed,null,null);

        storage.saveSoloAnswer(id,index,msg);
        index++;

        if (index >= SurveyQuestions.getTotalQuestions()) {
            List<String> answers =
                    SurveyQuestions.retrieveAnswersInOrder(storage,id);

            String mood = analyzer.determineMood(answers);
            List<String> artists = recommender.recommendSolo(mood);

            storage.resetSolo(id);
            return new AnswerResult(true,true,allowed,null,
                    new SoloResult(mood,artists));
        }

        storage.setSoloIndex(id,index);
        return new AnswerResult(true,false,allowed,
                new Question(
                        SurveyQuestions.getQuestion(index),
                        SurveyQuestions.getAllowedResponses(index),
                        index,id),
                null);
    }


    public void pauseSolo(String id) { storage.setSoloPaused(id,true); }

    public Question resumeSolo(String id) {
        if (!storage.isSoloActive(id)) return null;

        storage.setSoloPaused(id,false);
        int index = storage.getSoloIndex(id);

        return new Question(
                SurveyQuestions.getQuestion(index),
                SurveyQuestions.getAllowedResponses(index),
                index,id);
    }

    public void startPair(String id) {
        storage.setPairActive(true);
        storage.setPairPaused(true);
        storage.setPairUser1(id);
        storage.setPairUser2(null);
        storage.setPairIndex(0);
        storage.setPairTurn(1);
    }

    public Question joinPair(String id) {

        if (storage.getPairUser2() != null)
            return null;

        if (id.equals(storage.getPairUser1()))
            return null;

        storage.setPairUser2(id);
        storage.setPairPaused(false);

        return new Question(
                SurveyQuestions.getQuestion(0),
                SurveyQuestions.getAllowedResponses(0),
                0,
                storage.getPairUser1()
        );
    }


    public AnswerResultPair submitPairAnswer(String id, String msg) {
        String u1 = storage.getPairUser1();
        String u2 = storage.getPairUser2();
        int index = storage.getPairIndex();
        int turn = storage.getPairTurn();

        if (turn == 1 && !id.equals(u1))
            return new AnswerResultPair(false,false,null,u1,null,null,null,u1,u2);

        if (turn == 2 && !id.equals(u2))
            return new AnswerResultPair(false,false,null,u2,null,null,null,u1,u2);

        List<String> allowed = SurveyQuestions.getAllowedResponses(index);
        if (!allowed.contains(msg))
            return new AnswerResultPair(false,false,allowed,null,null,null,null,u1,u2);

        if (turn == 1) storage.savePairAnswerUser1(index,msg);
        else storage.savePairAnswerUser2(index,msg);

        index++;

        if (index >= SurveyQuestions.getTotalQuestions()) {
            List<String> a1 =
                    SurveyQuestions.retrieveAnswersInOrder(storage,u1,true);
            List<String> a2 =
                    SurveyQuestions.retrieveAnswersInOrder(storage,u2,false);

            String m1 = analyzer.determineMood(a1);
            String m2 = analyzer.determineMood(a2);
            List<String> artists =
                    recommender.recommendPair(m1,m2);

            storage.resetPair();
            return new AnswerResultPair(true,true,null,null,null,
                    new PairResult(m1,m2,artists),
                    null,u1,u2);
        }

        storage.setPairIndex(index);
        storage.setPairTurn(turn==1?2:1);

        String next =
                storage.getPairTurn()==1 ? u1 : u2;

        return new AnswerResultPair(true,false,null,null,
                new Question(
                        SurveyQuestions.getQuestion(index),
                        SurveyQuestions.getAllowedResponses(index),
                        index,next),
                null,next,u1,u2);
    }

    public void pausePair(String id) { storage.setPairPaused(true); }


    public Question resumePair(String id) {
        if (!storage.isPairActive()) return null;

        storage.setPairPaused(false);
        int index = storage.getPairIndex();

        String next =
                storage.getPairTurn()==1 ?
                        storage.getPairUser1() :
                        storage.getPairUser2();

        return new Question(
                SurveyQuestions.getQuestion(index),
                SurveyQuestions.getAllowedResponses(index),
                index,next);
    }

    public Question smartResume(String id) {
        if (storage.isSoloPaused(id)) return resumeSolo(id);
        if (storage.isPairPaused()) return resumePair(id);
        return null;
    }

    public String getPairUser1() { return storage.getPairUser1(); }
    public String getPairUser2() { return storage.getPairUser2(); }
}
