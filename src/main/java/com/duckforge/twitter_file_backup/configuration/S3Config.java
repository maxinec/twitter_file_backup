package com.duckforge.twitter_file_backup.configuration;

import lombok.Getter;
import lombok.Setter;

public class S3Config {
    @Getter @Setter private String region;
    @Getter @Setter private String bucket;
    @Getter @Setter private String filePrefix;
    @Getter @Setter private String accessKey;
    @Getter @Setter private String secretKey;
    @Getter @Setter private String awsProfileName;
}
