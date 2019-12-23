package com.duckforge.twitter_file_backup;

import com.duckforge.twitter_file_backup.configuration.TwitterLoaderConfig;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TweetStream implements Runnable {
    private static final Logger TWITTER = LogManager.getLogger("twitterOut");
    private static final Logger LOGGER = LogManager.getLogger("com.duckforge.twitterFileBackup.tweetStream");

    private final BasicClient hosebirdClient;
    private BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(100000);

    TweetStream(TwitterLoaderConfig config) {
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        if (!config.getTwitterIds().isEmpty()) {
            hosebirdEndpoint.followings(config.getTwitterIds());
        }
        if (!config.getTwitterTerms().isEmpty()) {
            hosebirdEndpoint.trackTerms(config.getTwitterTerms());
        }
        hosebirdEndpoint.languages(Arrays.asList("en"));

        Authentication hosebirdAuth = new OAuth1(
            config.getTwitterConsumerKey(),
            config.getTwitterConsumerSecret(),
            config.getTwitterToken(),
            config.getTwitterTokenSecret()
        );

        ClientBuilder builder = new ClientBuilder()
            .name("twitter-client")
            .hosts(hosebirdHosts)
            .authentication(hosebirdAuth)
            .endpoint(hosebirdEndpoint)
            .processor(new StringDelimitedProcessor(msgQueue));

        hosebirdClient = builder.build();
    }

    @Override
    public void run() {
        hosebirdClient.connect();
        while (!hosebirdClient.isDone()) {
            try {
                String msg = msgQueue.poll(5, TimeUnit.SECONDS);
                if (msg != null && !msg.isEmpty()) {
                    TWITTER.info(msg);
                }
            } catch (InterruptedException e) {
                return;
            }
        }
        LOGGER.error("Client connection closed unexpectedly: " + hosebirdClient.getExitEvent().getMessage());
    }

    void close() {
        LOGGER.info("SHUTDOWN");
        hosebirdClient.stop();
    }
}
