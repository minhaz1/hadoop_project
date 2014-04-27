import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class Tweet_Sentiment {
	
	public static String get_sentiment(String tweets){
		
		//Removing all url
		String commentstr1=tweets;
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr1);
        int i=0;
        while (m.find()) {
            commentstr1=commentstr1.replaceAll(m.group(i),"").trim();
            i++;
        }
        
        
        //Removing all non alpha-numeric characters
        commentstr1=commentstr1.replaceAll("[^a-zA-Z0-9\\s]","");
    
        //If there is little string
        if (commentstr1.length() < 4)
        	return null;

        return commentstr1;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if (args.length != 3){
			System.out.println(args.length);
			System.out.println("<path to file> <path to output> <api key>");
			System.exit(0);
		}

		JsonParser j_parse = new JsonParser();
		JsonObject json = null;
		
		
		try{
			//Opening the file to be read
			BufferedReader buffread = new BufferedReader(new FileReader(args[0]));
			BufferedWriter buffwrite = new BufferedWriter(new FileWriter(args[1]));
			
			String read_line;
			String sentiment;

			//reading line by line and format the tweets string
			while((read_line = buffread.readLine()) != null){
				String[] tweets = read_line.split("\t");

				if (tweets.length < 4)
					continue;
				URL obj = new URL("https://community-sentiment.p.mashape.com/text/");
				HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			
				con.setRequestMethod("POST");
				con.setRequestProperty("X-Mashape-Authorization","7TqZ31KLs4XbAhuTWp4n59RGluDzQWXO");
				con.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				
				String urlParameters = "txt="+get_sentiment(tweets[3]);
	    		wr.writeBytes(urlParameters);
				
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
		 
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				
				in.close();
				//print result
				
				json = ((JsonObject) j_parse.parse(response.toString())).get("result").getAsJsonObject();
				System.out.println(json.toString() + " : " + tweets[3]);
				buffwrite.write(read_line + "\t" + json.get("sentiment")+"|"+json.get("confidence"));
			}
			buffwrite.close();
			buffread.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
