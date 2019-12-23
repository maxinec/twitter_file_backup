package com.duckforge.twitter_file_backup.configuration;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ConfigBuilder {
    private static final String TWITTER_CONSUMER_KEY_CONFIG = "twitter.consumer_key";
    private static final String TWITTER_CONSUMER_SECRET_CONFIG = "twitter.consumer_secret";
    private static final String TWITTER_TOKEN_CONFIG = "twitter.token";
    private static final String TWITTER_TOKEN_SECRET_CONFIG = "twitter.token_secret";
    private static final String TWITTER_IDS_CONFIG = "twitter.ids";
    private static final String TWITTER_TERMS_CONFIG = "twitter.terms";
    private static final String S3_REGION = "s3.region";
    private static final String S3_BUCKET_CONFIG = "s3.bucket";
    private static final String S3_FILE_PREFIX = "s3.file_prefix";
    private static final String S3_ACCESS_KEY = "s3.access_key";
    private static final String S3_SECRET_KEY = "s3.secret_key";
    private static final String S3_AWS_PROFILE = "s3.aws_profile";
    private final Configuration config;
    private TwitterLoaderConfig twitterLoaderConfig;
    private S3Config s3Config;

    public ConfigBuilder(String propertiesFilePath) throws ConfigurationException {
        Parameters params = new Parameters();

        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
            new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(
                    params.properties()
                        .setFileName(propertiesFilePath)
                        .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                );

        this.config = builder.getConfiguration();
    }


    public TwitterLoaderConfig getTwitterLoaderConfig() throws ConfigurationException {
        if (twitterLoaderConfig != null)
            return twitterLoaderConfig;

        if (config.containsKey(TWITTER_CONSUMER_KEY_CONFIG) &&
            config.containsKey(TWITTER_CONSUMER_SECRET_CONFIG) &&
            config.containsKey(TWITTER_TOKEN_CONFIG) &&
            config.containsKey(TWITTER_TOKEN_SECRET_CONFIG) &&
            (config.containsKey(TWITTER_IDS_CONFIG) || config.containsKey(TWITTER_TERMS_CONFIG))) {

            twitterLoaderConfig = new TwitterLoaderConfig();
            twitterLoaderConfig.setTwitterConsumerKey(config.getString(TWITTER_CONSUMER_KEY_CONFIG));
            twitterLoaderConfig.setTwitterConsumerSecret(config.getString(TWITTER_CONSUMER_SECRET_CONFIG));
            twitterLoaderConfig.setTwitterToken(config.getString(TWITTER_TOKEN_CONFIG));
            twitterLoaderConfig.setTwitterTokenSecret(config.getString(TWITTER_TOKEN_SECRET_CONFIG));
            twitterLoaderConfig.setTwitterIds(config.getList(long.class, TWITTER_IDS_CONFIG));
            twitterLoaderConfig.setTwitterTerms(config.getList(String.class, TWITTER_TERMS_CONFIG));
            return twitterLoaderConfig;
        } else {
            throw new ConfigurationException("Missing required fields");
        }
    }

    public S3Config getS3Config() {
        if (s3Config != null)
            return s3Config;

        if (config.containsKey(S3_BUCKET_CONFIG)) {
            s3Config = new S3Config();
            s3Config.setRegion(config.getString(S3_REGION));
            s3Config.setBucket(config.getString(S3_BUCKET_CONFIG));
            if (config.containsKey(S3_FILE_PREFIX)) {
                String prefix = config.getString(S3_FILE_PREFIX);
                if (prefix.endsWith("/"))
                    s3Config.setFilePrefix(prefix);
                else
                    s3Config.setFilePrefix(prefix + "/");
            }
            s3Config.setAccessKey(config.get(String.class, S3_ACCESS_KEY, null));
            s3Config.setSecretKey(config.get(String.class, S3_SECRET_KEY, null));
            s3Config.setAwsProfileName(config.get(String.class, S3_AWS_PROFILE, null));
            return s3Config;
        }
        return null;
    }
}
