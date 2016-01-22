/**
 * @author Carter Currin
 * @file Downloader.java
 * @date 3/20/14
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class Downloader
{
	/**
	 * @description Downloads the .csv file from Yahoo Finances to /bin/symbol.csv
	 * 				The .csv file downloaded contains stock information from every
	 * 				business day from the current date back to 01/01/2013
	 * @param symbol : is the symbol of the company stocks to download
	 * @usage Downloader.doDownload("GOOG");
	 */
	public static void doDownload(String symbol)
	{
		DataInputStream in = null;
		FileOutputStream fOut = null;
		DataOutputStream out = null;
		String fileName = "bin" + File.separator + symbol + ".csv";
		
		try
		{
			int dateStartDay = 1;
			int dateStartMonth = 0; //January
			int dateStartYear = 2013;
			Calendar dateEnd = Calendar.getInstance();
			String dateAsString = String.format("&a=" + dateStartMonth + "&b=" +
								  dateStartDay + "&c=" + dateStartYear +
								  "&d=%1$tm&e=%1$te&f=%1$tY", dateEnd);
			String downloadURL = "http://ichart.finance.yahoo.com/table.csv?s=" +
								  symbol + dateAsString + "&g=d&ignore=.csv";
			URL remoteFile = new URL(downloadURL);
			URLConnection fileStream = remoteFile.openConnection();
			in = new DataInputStream(new BufferedInputStream(fileStream.getInputStream()));

			fOut = new FileOutputStream(fileName);
			out = new DataOutputStream(new BufferedOutputStream(fOut));

			int data;
			while ((data = in.read()) != -1)
			{
				out.write(data);
			}
		}
		catch (MalformedURLException e)
		{
			System.out.println("Please check the URL:" + e.toString());
		}
		catch (ConnectException e) {
			System.out.println(fileName.substring(0, fileName.length() - 3) +
							   ":failed! Connection Error!");
		}
		catch (FileNotFoundException e)
		{
			System.out.println("ERROR, make sure no other processes are using " + fileName
								+ " and run program again");
			System.out.println(fileName.substring(0, fileName.length() - 4) +
							   " will be skipped or innacurate");
			System.out.println(e);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				if (fOut != null)
					fOut.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
