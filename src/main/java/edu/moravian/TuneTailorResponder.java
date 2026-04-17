package edu.moravian;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import edu.moravian.exceptions.PairModeConflictException;
import edu.moravian.exceptions.SurveyAlreadyActiveException;

/**
 * This class will handle the types of messages that will be sent out to the user
 * while participating with the bot
 *
 * It uses responses from the TuneTailorResponder
 */
public class TuneTailorResponder extends ListenerAdapter {

    private static final String ALLOWED_CHANNEL = "log-testing";
    private final SurveyGame game;

    public TuneTailorResponder(SurveyGame game) {
        this.game = game;
    }

    /**
     * Used to respond to a user's input
     * @param e - user's input
     * @param msg - output message to user
     */
    private void reply(MessageReceivedEvent e, String msg) {
        e.getChannel().sendMessage(msg).queue();
    }

    /**
     * Handles when it is appropriate for the bot to respond to a message
     * @param e - Indication that a message was received
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        if (e.getAuthor().isBot()) return;
        if (!e.isFromGuild()) return;
        if (!(e.getChannel() instanceof net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel))
            return;

        String channelName = e.getChannel().getName().toLowerCase().trim();
        if (!channelName.equals(ALLOWED_CHANNEL)) return;

        handleDiscord(e);
    }

    /**
     * Handles different responses from different user input while
     * interacting with the bot
     *
     * All possible commands are handled and have responses
     *
     * @param e - Indication that a message was received
     */
    private void handleDiscord(MessageReceivedEvent e) {

        String id = e.getAuthor().getId();
        String msg = e.getMessage().getContentRaw().trim().toLowerCase();

        try {
            switch (msg) {

                case "!help" -> reply(e, TuneTailorResponses.help());

                case "!stop" -> {
                    game.forceStopAllSurveys(id);
                    reply(e, TuneTailorResponses.stopped());
                }


                case "!survey" -> {
                    try {
                        var q = game.startSolo(id);

                        if (q == null) {
                            reply(e, "⚠ You already have an active solo survey. Finish it or type `!stop`.");
                            return;
                        }

                        String mention = e.getAuthor().getAsMention();
                        String content = """
                                🟢 SOLO SURVEY |

                                %s You're up!

                                Q%d: %s
                                Options: %s
                                """.formatted(
                                mention,
                                q.getIndex() + 1,
                                q.getText(),
                                q.getAllowed()
                        );
                        reply(e, content);

                    } catch (SurveyAlreadyActiveException ex) {
                        reply(e, "⚠ You already have an active solo survey. Finish it or type `!stop`.");
                    } catch (PairModeConflictException ex) {
                        reply(e, TuneTailorResponses.soloBlockedByPair());
                    }
                }

                case "!pause solo" -> {
                    game.pauseSolo(id);
                    reply(e, TuneTailorResponses.soloPaused());
                }

                case "!resume solo" -> {
                    var q = game.resumeSolo(id);
                    String mention = e.getAuthor().getAsMention();
                    String content = """
                            🟢 SOLO SURVEY |

                            %s Resumed your solo survey!

                            Q%d: %s
                            Options: %s
                            """.formatted(
                            mention,
                            q.getIndex() + 1,
                            q.getText(),
                            q.getAllowed()
                    );
                    reply(e, content);
                }

                case "!pairsurvey" -> {
                    game.startPair(id);
                    String mention = e.getAuthor().getAsMention();
                    String content = """
                            🟣 PAIR SURVEY |

                            %s started a pair survey.
                            Type **!join** to participate.
                            """.formatted(mention);
                    reply(e, content);
                }

                case "!join" -> {
                    try {
                        if (id.equals(game.getPairUser1())) {
                            reply(e, "⚠ You're already hosting this pair survey.");
                            return;
                        }

                        var q = game.joinPair(id);

                        if (q == null) {
                            reply(e, "⚠ Pair is already full.");
                        } else {
                            String firstUserId = game.getPairUser1();
                            String firstMention = "<@" + firstUserId + ">";

                            String content = """
                                    🟣 PAIR SURVEY |

                                    %s You're up first!

                                    Q%d: %s
                                    Options: %s
                                    """.formatted(
                                    firstMention,
                                    q.getIndex() + 1,
                                    q.getText(),
                                    q.getAllowed()
                            );
                            reply(e, content);
                        }

                    } catch (SurveyAlreadyActiveException ex) {
                        reply(e, "⚠ Pair is already full.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        reply(e, "⚠ Internal error. Pair reset.");
                        game.forceStopAllSurveys(id);
                    }
                }

                case "!pause pair" -> {
                    game.pausePair(id);
                    reply(e, TuneTailorResponses.pairPaused());
                }

                case "!resume pair" -> {
                    var q = game.resumePair(id);
                    String nextId = q.getNextUser();
                    String nextMention = "<@" + nextId + ">";
                    String content = """
                            🟣 PAIR SURVEY |

                            %s Resumed your pair survey! You're up!

                            Q%d: %s
                            Options: %s
                            """.formatted(
                            nextMention,
                            q.getIndex() + 1,
                            q.getText(),
                            q.getAllowed()
                    );
                    reply(e, content);
                }

                case "!resume" -> {
                    var q = game.smartResume(id);
                    if (q == null) {
                        reply(e, TuneTailorResponses.noPaused());
                    } else {
                        String mention = e.getAuthor().getAsMention();
                        String content = """
                                🟢 SOLO SURVEY |

                                %s Resumed your solo survey!

                                Q%d: %s
                                Options: %s
                                """.formatted(
                                mention,
                                q.getIndex() + 1,
                                q.getText(),
                                q.getAllowed()
                        );
                        reply(e, content);
                    }
                }

                default -> handleAnswer(e, id, msg);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            reply(e, "⚠ Fatal error — surveys reset.");
            game.forceStopAllSurveys(id);
        }
    }

    /**
     * Handles user input during a survey and the answers the user inputs
     *
     * @param e - Indication that a message was received
     * @param id - User ID
     * @param msg - User message
     */
    private void handleAnswer(MessageReceivedEvent e, String id, String msg) {

        if (game.isSoloAnswerExpected(id)) {
            var r = game.submitSoloAnswer(id, msg);
            String mention = e.getAuthor().getAsMention();

            if (!r.isValid()) {
                reply(e, mention + "\n" + TuneTailorResponses.invalidSolo(r));
            } else if (r.isFinished()) {
                reply(e, mention + "\n" + TuneTailorResponses.soloFinished(r.getSoloResult()));
            } else {
                var q = r.getNextQuestion();
                String content = """
                        🟢 SOLO SURVEY |

                        %s You're up!

                        Q%d: %s
                        Options: %s
                        """.formatted(
                        mention,
                        q.getIndex() + 1,
                        q.getText(),
                        q.getAllowed()
                );
                reply(e, content);
            }
            return;
        }

        if (game.isPairAnswerExpected(id)) {
            var r = game.submitPairAnswer(id, msg);

            if (r.wrongTurnUser() != null) {
                reply(e, TuneTailorResponses.pairWrongUser(r.wrongTurnUser()));
                return;
            }

            if (!r.valid()) {
                String mention = e.getAuthor().getAsMention();
                reply(e, mention + "\n" + TuneTailorResponses.invalidPair(r));
                return;
            }

            if (r.finished()) {
                String mention = e.getAuthor().getAsMention();
                reply(e, mention + "\n" +
                        TuneTailorResponses.pairFinished(r.result(), r.user1(), r.user2()));
            } else {
                String nextId = r.nextUser();
                String nextMention = "<@" + nextId + ">";
                var q = r.nextQuestion();
                String content = """
                        🟣 PAIR SURVEY |

                        %s You're up!

                        Q%d: %s
                        Options: %s
                        """.formatted(
                        nextMention,
                        q.getIndex() + 1,
                        q.getText(),
                        q.getAllowed()
                );
                reply(e, content);
            }
            return;
        }

        reply(e, TuneTailorResponses.unknown());
    }

    /**
     * Test function to see how the bot responds to different user inputs
     * @param userId - user's id
     * @param input - user's input
     * @return A message to communicated to the user
     */
    public String handleTest(String userId, String input) {

        if (input == null || input.trim().isEmpty())
            return TuneTailorResponses.unknown();

        String msg = input.trim().toLowerCase();

        try {
            switch (msg) {

                case "!help" -> {
                    return TuneTailorResponses.help();
                }

                case "!stop" -> {
                    game.forceStopAllSurveys(userId);
                    return TuneTailorResponses.stopped();
                }


                case "!survey" -> {
                    var q = game.startSolo(userId);
                    if (q == null)
                        return TuneTailorResponses.soloBlockedByPair();
                    return TuneTailorResponses.soloStart(q);
                }

                case "!pause solo" -> {
                    game.pauseSolo(userId);
                    return TuneTailorResponses.soloPaused();
                }

                case "!resume solo" -> {
                    return TuneTailorResponses.soloResumed(game.resumeSolo(userId));
                }

                case "!pairsurvey" -> {
                    game.startPair(userId);
                    return TuneTailorResponses.pairLobby(userId);
                }

                case "!join" -> {
                    var q = game.joinPair(userId);
                    if (q == null)
                        return "Pair is already full.";
                    return TuneTailorResponses.pairStart(
                            q, game.getPairUser1(), game.getPairUser2());
                }

                case "!pause pair" -> {
                    game.pausePair(userId);
                    return TuneTailorResponses.pairPaused();
                }

                case "!resume pair" -> {
                    return TuneTailorResponses.pairResumed(game.resumePair(userId));
                }

                case "!resume" -> {
                    var q = game.smartResume(userId);
                    if (q == null)
                        return TuneTailorResponses.noPaused();
                    return TuneTailorResponses.nextSolo(q);
                }

                default -> {

                    if (game.isSoloAnswerExpected(userId)) {
                        var r = game.submitSoloAnswer(userId, msg);

                        if (!r.isValid())
                            return TuneTailorResponses.invalidSolo(r);
                        else if (r.isFinished())
                            return TuneTailorResponses.soloFinished(r.getSoloResult());
                        else
                            return TuneTailorResponses.nextSolo(r.getNextQuestion());
                    }

                    if (game.isPairAnswerExpected(userId)) {
                        var r = game.submitPairAnswer(userId, msg);

                        if (r.wrongTurnUser() != null)
                            return TuneTailorResponses.pairWrongUser(r.wrongTurnUser());
                        if (!r.valid())
                            return TuneTailorResponses.invalidPair(r);
                        if (r.finished())
                            return TuneTailorResponses.pairFinished(
                                    r.result(), r.user1(), r.user2());
                        return TuneTailorResponses.nextPair(
                                r.nextQuestion(), r.nextUser());
                    }

                    return TuneTailorResponses.unknown();
                }
            }

        } catch (Exception ex) {
            game.forceStopAllSurveys(userId);
            return "⚠ Something went wrong — surveys reset.";
        }
    }
}
