package edu.moravian;

import edu.moravian.secrets.Secrets;
import edu.moravian.secrets.SecretsException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import redis.clients.jedis.JedisPool;

/**
 * Class used to create and construct all neccesary components for the bot to function
 *
 * Launches the bot when all components have been created
 *
 */
public class TuneTailorBot {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting TuneTailor Bot...");

        String token;
        try {
            Secrets secrets = new Secrets();
            token = secrets.getSecret("220_Discord_Token", "DISCORD_TOKEN");
            System.out.println("AWS SECRET TOKEN FOUND = " + (token != null && !token.isBlank()));
        } catch (SecretsException e) {
            throw new IllegalStateException("Could not load DISCORD_TOKEN from AWS Secrets Manager", e);
        }

        if (token == null || token.isBlank()) {
            throw new IllegalStateException("DISCORD_TOKEN is missing from AWS Secrets Manager");
        }

        SurveyStorage storage;
        try {
            JedisPool pool = new JedisPool("localhost", 6379);
            storage = new RedisSurveyStorage(pool);
        } catch (RuntimeException e) {
            System.out.println("Redis could not be initialized. Switching permanently to Memory Storage.");
            storage = new MemorySurveyStorage();
        }

        MoodAnalyzer moodAnalyzer = new MoodAnalyzer();
        ArtistRecommender recommender = new ArtistRecommender();
        SurveyGame game = new SurveyGame(storage, moodAnalyzer, recommender);

        JDABuilder builder = JDABuilder.createDefault(
                token,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
        );

        builder.addEventListeners(new BotListener(game));
        builder.build();

        System.out.println("TuneTailor Bot running...");
    }

    static class BotListener extends net.dv8tion.jda.api.hooks.ListenerAdapter {

        private final SurveyGame game;

        public BotListener(SurveyGame game) {
            this.game = game;
        }

        @Override
        public void onMessageReceived(net.dv8tion.jda.api.events.message.MessageReceivedEvent e) {
            new TuneTailorResponder(game).onMessageReceived(e);
        }
    }
}