package MiniStressTester;

import java.util.ArrayList;

public class Twitter {
	
	ArrayList<String> stops;
	ArrayList<ArrayList<String>> wordsOfTwts;
	MyHashTable<String, Tweet> tweetTable; //every author only has 1 tweet, latest tweet
	MyHashTable<String, Tweet> tweetSortingTable; //twts by date
	MyHashTable<String, ArrayList<Tweet>> tweetDateTable; //twtLists by date
	MyHashTable<String, Integer> trendingTweetTable;
	MyHashTable<String, String> stopTable;

	// O(n+m) where n is the number of tweets, and m the number of stopWords
	public Twitter(ArrayList<Tweet> tweets, ArrayList<String> stopWords) {
		tweetTable = new MyHashTable<>(tableSize(tweets.size()));
		tweetSortingTable = new MyHashTable<>(tableSize(tweets.size()));
		wordsOfTwts = new ArrayList<>();
		int numWords=0;
		for (Tweet twt : tweets){
			//LATEST TWEET BY AUTHOR INIT
			Tweet oldTwt = tweetTable.put(twt.getAuthor(), twt);
			String testDT = null;
			if (oldTwt != null) {
				testDT = oldTwt.getDateAndTime();
			}
				//if oldTwt is a more recent tweet, put it back
			if (testDT != null && testDT.compareTo(twt.getDateAndTime()) > 0) {
				tweetTable.put(twt.getAuthor(), oldTwt);
			}

			//TWEETS BY DATE INIT
			tweetSortingTable.put(twt.getDateAndTime(), twt);

			//TRENDING TWEETS INIT
			ArrayList<String> words = getWords(twt.getMessage());
			wordsOfTwts.add(words);
			numWords += words.size();
		}

		//TWEETS BY DATE
		ArrayList<Tweet> tweetDates = new ArrayList<>();
		ArrayList<String> dates = MyHashTable.fastSort(tweetSortingTable); //sorting all tweets by date
		for (String d : dates){
			tweetDates.add(tweetSortingTable.get(d));
		}
		tweetDateTable = new MyHashTable<>(tweets.size()); //the size is wrong but w/e
		int x=0;
			//going thru tweetDates, putting them into AL by date
		while (x < tweetDates.size()-1){
			ArrayList<Tweet> twtsByDates = new ArrayList<>();
			twtsByDates.add(tweetDates.get(x));
			String date = tweetDates.get(x).getDateAndTime().substring(0, 10);
			boolean nextDate = true;
			while (x < tweetDates.size()-1 && tweetDates.get(x+1).getDateAndTime().substring(0, 10).equals(date)){
				twtsByDates.add(tweetDates.get(x+1));
				x++;
				nextDate = false;
			}
			if (nextDate && x < tweetDates.size()-1) {x++;}
			tweetDateTable.put(date, twtsByDates);
		}

		//TRENDING TOPICS
		stops = stopWords;
		stopTable = new MyHashTable<>(tableSize(stops.size()));
		for (String stop : stops){
			stop = stop.toLowerCase();
			stopTable.put(stop, stop);
		}
		trendingTweetTable = new MyHashTable<>(tableSize(numWords));
		for (ArrayList<String> twtMsg : wordsOfTwts){
			MyHashTable<String, Boolean> counted = new MyHashTable<>(twtMsg.size());
			for (String word : twtMsg){
				word = word.toLowerCase();
				if (stopTable.get(word) == null){
					if (counted.get(word) == null || !counted.get(word)) {
						if (trendingTweetTable.get(word) == null) {
							trendingTweetTable.put(word, 1);
						} else {
							int output = trendingTweetTable.get(word);
							trendingTweetTable.put(word, output+1);
						}
						counted.put(word, true);
					}
				}
			}
		}
	}

	private int tableSize(int numEntries){
		if (numEntries == 0){
			return 1;
		}
		int size = (int) (numEntries/0.75 + 0.5);
		return size;
	}

	public void addTweet(Tweet t) {
		//AUTHOR
		Tweet oldTwt = tweetTable.put(t.getAuthor(), t);
		String testDT = null;
		if (oldTwt != null) {
			testDT = oldTwt.getDateAndTime();
		}
		if (testDT != null && testDT.compareTo(t.getDateAndTime()) > 0) {
			tweetTable.put(t.getAuthor(), oldTwt);
		}
		//BY DATE
		ArrayList<Tweet> thisTweet = new ArrayList<>(); thisTweet.add(t);
		ArrayList<Tweet> test = tweetDateTable.put(t.getDateAndTime().substring(0, 10), thisTweet);
		if (test != null){
			test.add(t);
			tweetDateTable.put(t.getDateAndTime().substring(0, 10), test);
		}
		//TRENDING
		ArrayList<String> words = getWords(t.getMessage());
		wordsOfTwts.add(words);
		MyHashTable<String, Boolean> counted = new MyHashTable<>(words.size());
		for (String word : words){
			word = word.toLowerCase();
			if (stopTable.get(word) == null){
				if (counted.get(word) == null || !counted.get(word)) {
					if (trendingTweetTable.get(word) == null) {
						trendingTweetTable.put(word, 1);
					} else {
						int output = trendingTweetTable.get(word);
						trendingTweetTable.put(word, output+1);
					}
					counted.put(word, true);
				}
			}
		}
	}

	//compareTo returns positive if 1st string is numerically > 2nd string,
	//and if 1st string is alphabetically <  2nd string (caps are greater than lowercase)
    public Tweet latestTweetByAuthor(String author) {
        Tweet latestTweet = tweetTable.get(author);
    	return latestTweet;
    }

    public ArrayList<Tweet> tweetsByDate(String date) {
        ArrayList<Tweet> twtsByDate = tweetDateTable.get(date);
        if (twtsByDate == null || twtsByDate.size() == 0){
        	return null;
		}
        return twtsByDate;
    }

    //TEST TRENDING TOPICS
    public ArrayList<String> trendingTopics() {
		ArrayList<String> trendingWords = MyHashTable.fastSort(trendingTweetTable);
		//System.out.println(trendingTweetTable.values().toString());
		return trendingWords;
    }

    /**
     * An helper method you can use to obtain an ArrayList of words from a 
     * String, separating them based on apostrophes and space characters. 
     * All character that are not letters from the English alphabet are ignored. 
     */
    private static ArrayList<String> getWords(String msg) {
    	msg = msg.replace('\'', ' ');
    	String[] words = msg.split(" ");
    	ArrayList<String> wordsList = new ArrayList<String>(words.length);
    	for (int i=0; i<words.length; i++) {
    		String w = "";
    		for (int j=0; j< words[i].length(); j++) {
    			char c = words[i].charAt(j);
    			if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
    				w += c;
    		}
    		wordsList.add(w);
    	}
    	return wordsList;
    }

}
