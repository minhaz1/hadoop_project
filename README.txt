Files:

├submission
├── flume
│   ├── flume-file.conf
│   └── flume-hdfs.conf
├── hive
│   ├── h2.hql
│   └── hive.hql
├── src
│   ├── gt-shapefile-11.0.jar
│   ├── json-lib-2.4-jdk15.jar
│   ├── json-simple-1.1.1.jar
│   ├── twitter4j-core-4.0.1.jar
│   ├── twitter4j.properties
│   ├── twitter4j-stream-4.0.1.jar
│   ├── twitter_dump.properties
│   └── Twitter_Public_Stream.java
└── tweets - container for tweets and spooling directory for flume


Sourcing the aliases file makes everything simpler:

alias compile='javac -cp \* Twitter_Public_Stream.java'
alias run='java -cp .:\* Twitter_Public_Stream twitter_dump.properties'

The first alias compiles the code to download tweets.
The second will run the java program to download tweets which receives 5 arguments when using the run alias.

Example: run -71.191208 42.227859 -70.808517 42.397259 BOSTON &> BOSTON.log &
	 run SW_long	SW_lat	  NE_long    NE_lat    NAME   &> NAME.log &

This will download tweets and clean them up and put it in the tweets directory.
Example:
BUFFALO	  42.896261499999994 -78.8558615 Wed Apr 23 17:45:27 UTC 2014	we're escaping early #Turnup http://t.co/XO6FPKWBB9

From there a pair of flume agents running with an avro sink/source that will copy the files into HDFS into /flume/events

To start flume:

HDFS:
flume-ng agent --conf conf --conf-file flume-hdfs.conf --name a1
File:
flume-ng agent --conf conf --conf-file flume-file.conf --name a1

Once the data is in HDFS, a pair of hive scripts are run that perform the following tasks:
     1. Load the data into a hive table.
     2. Count the number of occurrences of each word in the text of the tweets.
     3. Find the most common hash tag in each region.

This generates a TSV file...

The tsv file is then sent to our web server. 
The web server has a php script that generates a custom google maps
page and maps the hash tags and their locations on the map and will show
users what was trending at the time. 
