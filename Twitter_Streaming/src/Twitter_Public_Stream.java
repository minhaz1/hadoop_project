/*
 * Author: Sothiara Em
 * Email: sothiara@gmail.com 
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class Twitter_Public_Stream implements StatusListener{

	//JSONObject to write custom json output
	private JSONObject jsonObject;
	private JSONArray jsonArray;
	
	private String config_file_path;
	

	//Current counter
	private int CURR_MAX_FILE = 0;
	private int CURR_EVENTS = 0;
	private int CURR_BYTE_SIZE = 0;
	
	
	//Configuration Parameters
	private File json_out;
	private String file_path = System.getProperty("user.dir");
	private String file_name = "twitter-stream";
	private int MAX_EVENTS = 0;
	private int MAX_FILE = 10;
	//Max file size before rolling out to a new files
	private long MAX_BYTE_SIZE = 2048;
	
	//Location lookup
	private Reverse_Geocode rev_geocode;
	
	
	private BufferedWriter buff_write;
	
	/**
	 * Constructor to takes in the file_path for the configuration file
	 * if no configuration file is given, use the default value
	 * @param file_path
	 */
	public Twitter_Public_Stream(String file_path){
		
		//Initialize the reverse lookup map
		rev_geocode = new Reverse_Geocode("us_states\\states.shp");
		
		if (file_path != null){
			this.config_file_path = file_path;
			set_configuration();
		}
		
		json_out = new File(this.file_path+"\\"+file_name+"_"+System.currentTimeMillis()+".txt");
		try {
			buff_write = new BufferedWriter(new FileWriter(json_out));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onException(Exception arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStallWarning(StallWarning arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void flush_file(){
		try {
			
			buff_write.flush();
			buff_write.close();
			//Timestamp the file
			json_out = new File(file_path+"\\"+file_name+"_"+System.currentTimeMillis()+".txt");
			
			CURR_EVENTS = 0;
			CURR_BYTE_SIZE = 0;
			
			//Keep writing out different files
			if (MAX_FILE != 0){
				CURR_MAX_FILE ++;
				if (CURR_MAX_FILE == MAX_FILE)
					System.exit(0);
			}
			
			buff_write = new BufferedWriter(new FileWriter(json_out));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onStatus(Status status) {
		
		//Json object json array to store custom queries
		jsonObject = new JSONObject();
		jsonArray = new JSONArray();
		
		//Flush the file only if theres a limit to max_event (not equal to 0)
		if (MAX_EVENTS != 0){
			if (CURR_EVENTS == MAX_EVENTS)
				flush_file();
		}
		
		/*
		//Get the screen name
		String screen_name = status.getUser().getScreenName();
		
		//Hash tag in the text
		HashtagEntity[] hash_entities = status.getHashtagEntities();
		
		//Getting the location 
		GeoLocation location = status.getGeoLocation();
		
		//Text of the tweet
		String text = status.getText();
		
		for (int i = 0;i<hash_entities.length;i++){
			jsonArray.add(hash_entities[i].getText());
		}
		
		jsonObject.put("screen_name",screen_name);
		jsonObject.put("hash_entities", jsonArray);
		jsonObject.put("location", location);
		jsonObject.put("text", text);
		 */
		
		//Getting the location 
		GeoLocation location = status.getGeoLocation();

		//For now we are storing the raw json file
		try {
			String raw_json = TwitterObjectFactory.getRawJSON(status);
			
			if (MAX_EVENTS == 0){
				if (MAX_BYTE_SIZE !=0){
					if (CURR_BYTE_SIZE + raw_json.length() >= MAX_BYTE_SIZE){
						flush_file();
					}
				}
			}
			buff_write.write(raw_json);
			/* Testing location filtering and match it with reverse lookup
			buff_write.newLine();
			String state_location = rev_geocode.getStateCode(status.getGeoLocation().getLongitude(),status.getGeoLocation().getLatitude());
			buff_write.write(state_location);
			*/
			buff_write.newLine();
			
			if (MAX_EVENTS != 0){
				CURR_EVENTS ++;
			}else{
				if (MAX_BYTE_SIZE != 0)
					CURR_BYTE_SIZE+=raw_json.length();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * This method is used to load the configuration from a configu file
	 */
	private void set_configuration(){
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {
	 
			input = new FileInputStream(config_file_path);
			// load a properties file
			prop.load(input);
	 
			//Look at and apply the properties
			if (prop.getProperty("max_events") != null)
				this.MAX_EVENTS = Integer.parseInt(prop.getProperty("max_events"));
			
			//Setting the maximum of files to roll over
			if (prop.getProperty("max_log") != null)
				this.MAX_FILE = Integer.parseInt(prop.getProperty("max_log"));
			
			//Setting the output directory
			if (prop.getProperty("output_directory") != null)
				file_path = prop.getProperty("output_directory").replace("\"", "");
			
			//Setting the prepend file name
			if (prop.getProperty("filename") != null)
				file_name = prop.getProperty("filename").replace("\"", "");
			
			//Setting the max file size before rolling out
			if (prop.getProperty("max_file_size") !=null)
				MAX_BYTE_SIZE = Long.parseLong(prop.getProperty("max_file_size"));
			
	 
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//Setting the filter to only get tweets from New-York
		double[][] location_filter ={{-74.0,40.0},{-73.0,41.0}};
		FilterQuery fq = new FilterQuery();
		fq.locations(location_filter);
		
		//Initialize twitter streaming
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		String config;
		if (args.length == 0)
			config = null;
		else
			config = args[0];
		StatusListener listener = new Twitter_Public_Stream(config);

		twitterStream.addListener(listener);
		twitterStream.filter(fq);
	}
}


