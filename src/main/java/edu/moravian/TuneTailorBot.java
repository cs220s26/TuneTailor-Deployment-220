package edu.moravian;


import edu.moravian.secrets.Secrets;
import edu.moravian.secrets.SecretsException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import redis.clients.jedis.JedisPool;

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

        // Selects Redis storage if available, otherwise falls back to memory storage
        SurveyStorage storage;
        try {
            JedisPool pool = new JedisPool("localhost", 6379);
            storage = new RedisSurveyStorage(pool);
            System.out.println("Redis connected successfully.");
        } catch (RuntimeException e) {
            System.out.println("Redis offline. Switching permanently to Memory Storage.");
            storage = new MemorySurveyStorage();
        }

        // Creates the core game components
        MoodAnalyzer moodAnalyzer = new MoodAnalyzer();
        ArtistRecommender recommender = new ArtistRecommender();
        SurveyGame game = new SurveyGame(storage, moodAnalyzer, recommender);

        // Builds and starts the Discord bot with required intents
        JDABuilder builder = JDABuilder.createDefault(
                token,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
        );

        // Registers the message listener
        builder.addEventListeners(new BotListener(game));
        builder.build();

        System.out.println("TuneTailor Bot running...");
    }

    // Forwards Discord messages to the TuneTailorResponder
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
