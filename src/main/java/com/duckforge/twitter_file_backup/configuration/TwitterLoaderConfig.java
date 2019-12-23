package com.duckforge.twitter_file_backup.configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class TwitterLoaderConfig {
    @Getter @Setter private String twitterConsumerKey;
    @Getter @Setter private String twitterConsumerSecret;
    @Getter @Setter private String twitterToken;
    @Getter @Setter private String twitterTokenSecret;
    @Getter @Setter private List<Long> twitterIds;
    @Getter @Setter private List<String> twitterTerms;
}
