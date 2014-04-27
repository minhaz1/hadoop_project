DROP TABLE IF EXISTS tweets;

CREATE EXTERNAL TABLE tweets (state STRING, latlong STRING, time STRING, text STRING)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
LOCATION '/flume/events/tweets/';

DROP TABLE IF EXISTS results;

CREATE EXTERNAL TABLE results (state STRING, latlong STRING, word STRING, number INT)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
LOCATION '/hive-out0';

INSERT OVERWRITE TABLE results
SELECT  state, latlong, lower(word), COUNT(*) as count FROM tweets
       LATERAL VIEW explode(split(text, ' ')) lTable as word
GROUP BY state,word,latlong
ORDER BY state, count;