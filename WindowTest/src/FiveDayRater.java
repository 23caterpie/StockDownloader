/**
 * @author Carter Currin
 * @file FiveDayRater.java
 * @date 3/20/14
 */
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.Exception;

import au.com.bytecode.opencsv.CSVReader;

public class FiveDayRater
{
	private ArrayList<HistoryQuote> recentQuotes;
	private String symbol;
	
	public FiveDayRater()
	{
		recentQuotes = null;
		symbol = "";
	}
	
	/**
	 * @description makes a FiveDayRater for symbol.csv and fills recentQuotes with
	 * 				HistoryQuotes representing the most most recent 6 business days
	 * @param theSymbol is a String representing the file name of the .csv file to
	 * 					be rated without file extension
	 * @usage FiveDayRater currentRating = new FiveDayRater("GOOG");
	 */
	public FiveDayRater(String theSymbol)
	{
		symbol = theSymbol;
		try
		{
		fillRecentQuotes();
		}
		catch(Exception except)
		{
			System.out.println(except.getMessage());
		}
	}
	
	/**
	 * @description A helper method that fills recentQuotes with HistoryQuotes from
	 * 				the 6 most recent business days from symbol.csv
	 * @throws an Exception if the file symbol.csv is not found
	 * @usage fillRecentQuotes();
	 */
	private void fillRecentQuotes() throws Exception
	{
		URL quoteToRate = FiveDayRater.class.getResource(symbol + ".csv");
		if(quoteToRate == null)
			throw new Exception(symbol + ".csv not found");
		String filesPathAndName = quoteToRate.getPath(); 
		CSVReader reader;
		try
		{
			recentQuotes = new ArrayList<HistoryQuote>(6);
			HistoryQuote nextQuote = new HistoryQuote();
			
			reader = new CSVReader(new FileReader(filesPathAndName), ',', '"', 1);
			String[] nextQuoteString;
			for(int i = 0; i < 6; i++)
			{
		        nextQuoteString = reader.readNext();
		        nextQuote.setDate(nextQuoteString[0]);
		        nextQuote.setOpen(Double.parseDouble(nextQuoteString[1]));
		        nextQuote.setHigh(Double.parseDouble(nextQuoteString[2]));
		        nextQuote.setLow(Double.parseDouble(nextQuoteString[3]));
		        nextQuote.setClose(Double.parseDouble(nextQuoteString[4]));
		        nextQuote.setVolume(Integer.parseInt(nextQuoteString[5]));
		        recentQuotes.add(i, new HistoryQuote(nextQuote));
		    }
			reader.close();
		}  
		catch(IOException e)
		{  
			e.printStackTrace();
		}
		catch(Exception e)
		{  
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * @description gives a stock .csv file a rating based on trends of the most
	 * 				recent 5 days as such for each day:
	 * 					a. Price is higher than previous, volume is higher, +2
	 * 					b. Price is higher than previous, volume is lower, +1
	 * 					c. Price is lower than previous, volume is lower, -1
	 * 					d. Price is lower than previous, volume is higher, -2
	 * 				the sum of each day's ratings is the rating to be returned
	 * @throws an Exception if there are less than 6 HistoryQuotes in recentQuotes
	 * @usage System.out.println(currentRating.getRating());
	 */
	public int getRating() throws Exception
	{
		if(recentQuotes == null)
			throw new Exception("There are no quotes to rate.");
		if(recentQuotes.size() < 6)
			throw new Exception("There are not enough quotes to rate.");
		int ratingTotal = 0;
		int ratingNext;
		for(int i = 0; i < 5; i++)
		{
			if(recentQuotes.get(i).getClose() > recentQuotes.get(i + 1).getClose())
				if(recentQuotes.get(i).getVolume() > recentQuotes.get(i + 1).getVolume())
					ratingNext = 2;
				else
					ratingNext = 1;
			else if(recentQuotes.get(i).getVolume() > recentQuotes.get(i + 1).getVolume())
				ratingNext = -2;
			else
				ratingNext = -1;
			ratingTotal += ratingNext;
		}
		return ratingTotal;
	}
}
