DROP TABLE IF EXISTS final;

CREATE EXTERNAL TABLE final (latlong STRING, state STRING, word STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
LOCATION '/hive-out';

INSERT OVERWRITE TABLE final
select results.latlong, results.state, results.word from results
       join (
       	    select state, max(number) maxNum from results
	    where substr(word,1,1)="#"
	    group by state
	    ) s
on results.state = s.state and results.number=s.maxNum
   and substr(results.word,1,1)="#"
--   and lower(results.word) != "the"
--   and lower(results.word) != "in"
--   and lower(results.word) != "i"
--   and lower(results.word) != "for"
group by results.latlong, results.state, results.word;