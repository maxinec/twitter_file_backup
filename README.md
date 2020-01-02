![](https://github.com/maxinec/twitter_file_backup/workflows/Java%20CI/badge.svg)
 
# Twitter File Backup
Twitter File Backup stores the output of /statuses/filter.json to file, with an option to upload the files to S3.

### Build instructions
```
gradle clean fatJar # will generate build/libs/twitterFileBackup-all-1.0.jar
```

### Configuration
**Required configuration**  
For Twitter, AWS properties:
https://github.com/maxinec/twitter_file_backup/blob/master/config_example.properties  
**Note:** If S3 properties are not set, Twitter File Backup will still write the output of /statuses/filter.json to file.

**Optional configuration**  
Twitter File Backup uses log4j to store tweets to file.  Edit the [log4j xml](https://github.com/maxinec/twitter_file_backup/blob/master/src/main/resources/log4j2.xml) 
to modify how files rollover, naming, etc.

To change the directory where the files are stored, pass a Java VM option at runtime: 
```-DlogFolder=<directory>```

### Run instructions
``` 
java -DlogFolder=<directory to store the twitter files> -cp twitterFileBackup.jar com.duckforge.twitter_file_backup.TwitterFileBackup <config location> 
```
