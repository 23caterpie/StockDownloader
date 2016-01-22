/**
 * @author Carter Currin
 * @file HistoryQuote.java
 * @date 3/20/14
 */
public class HistoryQuote { 
	
	private String date;
	private double high;
	private double low;
	private double open;
	private double close;
	private int volume;
	
	public HistoryQuote()
	{
		date = "";
		high = 0;
		low = 0;
		open = 0;
		close = 0;
	}
	
	public HistoryQuote(HistoryQuote oldQuote)
	{
		this.date = oldQuote.date;
		this.high =  oldQuote.high;
		this.low =  oldQuote.low;
		this.open =  oldQuote.open;
		this.close =  oldQuote.close;
	}
	
	public void setDate(String newDate)
	{
		date = newDate;
	}
	
	public void setHigh(double newHigh)
	{
		high = newHigh;
	}
	
	public void setLow(double newLow)
	{
		low = newLow;
	}
	
	public void setOpen(double newOpen)
	{
		open = newOpen;
	}
	
	public void setClose(double newClose)
	{
		close = newClose;
	}
	
	public void setVolume(int newVolume)
	{
		volume = newVolume;
	}
	
	public String getDate()
	{
		return date;
	}
	
	public double getHigh()
	{
		return high;
	}
	
	public double getLow()
	{
		return low;
	}
	
	public double getOpen()
	{
		return open;
	}
	
	public double getClose()
	{
		return close;
	}
	
	public int getVolume()
	{
		return volume;
	}
	
	public String toString()
	{
		return ("date: " + date + "\nhigh: " + high + "\nlow: " + low + "\nopen: " +
				open + "\nclose: " + close + "\nvolume: " + volume);
	}
	
	public boolean equals(Object comparisonObject)
	{
		if(getClass() != comparisonObject.getClass())
			return false;
		return (this.date.equals(((HistoryQuote)comparisonObject).date) &&
				this.high == ((HistoryQuote)comparisonObject).high &&
				this.low == ((HistoryQuote)comparisonObject).low &&
				this.open == ((HistoryQuote)comparisonObject).open &&
				this.close == ((HistoryQuote)comparisonObject).close &&
				this.volume == ((HistoryQuote)comparisonObject).volume);
	}
}
