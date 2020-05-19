package com.rcastech.alcanzativo;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.DirectMessage;
import twitter4j.FilterQuery;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import static twitter4j.TwitterMethod.UPDATE_STATUS;

public class TwitterAutoBot {
	
	static Twitter twitter = TwitterFactory.getSingleton();
	private static final Object LOCK = new Object();
	
    public static void main(String[] args)throws TwitterException {
    	
    	readTweet();
    	/*
    	 * se crea la instancia usando las propiedades en un archivo twitter4j.properties
    	 * este debe contener las siguientes llaves:
    	 * debug=true
    	 * oauth.consumerKey=
    	 * oauth.consumerSecret=
    	 * oauth.accessToken=
    	 * oauth.accessTokenSecret=
    	 * 
    	 * las llaves se obtienen desde el dashboard de twitter developers
    	*/
    	TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
    	
    	
    	StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
    	
        twitterStream.addListener(listener);
        
    	FilterQuery query = new FilterQuery();
        query.follow(new long[] { twitterStream.getId() });
        twitterStream.filter(query);

    }
   
    
    private static void tweetLines() {
        String line;
        try {
            try (
            		
                    InputStream fis = new FileInputStream("resources\\tweets.txt");
                    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("Cp1252"));
                    BufferedReader br = new BufferedReader(isr);
            ) {
                while ((line = br.readLine()) != null) {
                    // Deal with the line
                    sendTweet(line);
                    System.out.println("Tweeting: " + line + "...");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    private static void sendTweet(String line) {
    	
        Status status;
        try {
            status = twitter.updateStatus(line);
            System.out.println(status);
        } catch (TwitterException e) {;
            e.printStackTrace();
        }
    }
    
    private static void readTweet(){
    
    	try{
    		
    		ResponseList<Status> responseList=twitter.getMentionsTimeline();
    		responseList.forEach(s->{
    			
    			System.out.println(s.getUser().getScreenName()+"  : "+s.getText()+ " fecha "+ s.getCreatedAt().toString());
    		
    		});
    	}catch(TwitterException e){
    		e.printStackTrace();
    	}
    }
}
