/**
 * @author Carter Currin
 * @file FiveDayRating.java
 * @date 4/3/14
 */
public class FiveDayRating implements Comparable<FiveDayRating>
{
	private int rating;
	private String symbol;
	
	public FiveDayRating()
	{
		rating = 0;
		symbol = "EMPTY";
	}
	
	public FiveDayRating(int newrating, String newSymbol)
	{
		rating = newrating;
		symbol = newSymbol;
	}
	
	public int compareTo(FiveDayRating otherRating)
	{
		int compareRating = otherRating.getRating();
		return  compareRating - this.rating;
	}
	
	public int getRating()
	{
		return rating;
	}
	
	public String getSymbol()
	{
		return symbol;
	}
	
	public void setRating(int newRating)
	{
		rating = newRating;
	}
	
	public void setSymbol(String newSymbol)
	{
		symbol = newSymbol;
	}
}
