/**
 * @author Carter Currin
 * @file MySQLAccess.java
 * @date 4/3/14
 */
import java.sql.*;
import java.util.ArrayList;

public class MySQLAccess {
  private Connection connect = null;
  private Statement statement = null;
  private ResultSet resultSet = null;
  private ArrayList<String> symbolList;

  /**
   * @description Reads the ada.gonzaga.edu database's table Dow30 and returns each
   * element in the "Symbol" Column in an ArrayList
   */
  public ArrayList<String> readDataBase() throws Exception {
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      connect = DriverManager
          .getConnection("jdbc:mysql://ada.gonzaga.edu/cs224", "cs224","cs2241234.");

      statement = connect.createStatement();
      resultSet = statement.executeQuery("select * from Dow30");
      symbolList = new ArrayList<String>();
      writeResultSet(resultSet);
    
    }
    catch (Exception e)
    {
      throw e;
    }
    finally
    {
      close();
    }
    return new ArrayList<String>(symbolList);
  }

  private void writeResultSet(ResultSet resultSet) throws SQLException {
    while (resultSet.next())
    {
      String symbol = resultSet.getString("Symbol");
      symbolList.add(symbol);
    }
  }

  private void close() {
    try {
	  if (resultSet != null)
	  {
	    resultSet.close();
	  }
	
	  if (statement != null)
	  {
	    statement.close();
	  }
	
	  if (connect != null)
	  {
	    connect.close();
	  }
    }
    catch (Exception e)
    {

    }
  }
  public static void main(String[] args) throws Exception {
            MySQLAccess dao = new MySQLAccess();
            dao.readDataBase();
  }

} 
