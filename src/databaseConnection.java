import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
//import java.sql.Date;
import java.util.Date;

public class databaseConnection {

	 private final String projectPath = System.getProperty("user.dir");
	 private final String userName;
	 private final String passWord; 
	 private final String sqlDropTable = "DROP TABLE tblAlarm";
	 private final String sqlSelect = "SELECT * FROM tblAlarm ORDER BY date, time ASC";
//	 private final String sqlInsert = "INSERT INTO TBLALARM (subject, body, sound, Date, Time) VALUES ('Assignment 1', 'get it done by Saturday', 'bluesky.wav', '2017-05-10', '12:30:00')";
	 private final String sqlCreateTable = "CREATE TABLE tblAlarm ( id INT NOT NULL GENERATED ALWAYS AS IDENTITY, subject varchar(255) NOT NULL, body varchar(255), sound varchar(100) NOT NULL, Date date,Time time, primary key (id))";
	 private static Statement stmt;
	 private PreparedStatement sqlPrepare = null;
	 private final Connection dbCon;
	 private final String host;
	 
    public databaseConnection(String databaseName, String username, String password)throws SQLException {
    	userName = username;
    	passWord = password;
        host = "jdbc:derby://localhost:1527/" + projectPath + "/" + databaseName +
    		   ";create=true;user=" + username + ";/password=" + password;          
        
       dbCon = DriverManager.getConnection(host, userName, password); 
       try{	    
    	   stmt = dbCon.createStatement();	
//    	   System.out.print("ok in db");
//    	   stmt.executeUpdate(sqlDropTable);
//    	   if (tableExist(dbCon) == false) System.out.println("true or false");
//    	   		stmt.executeUpdate(sqlCreateTable);
    	   
       }catch(SQLException ex){
            ex.printStackTrace();
       }
      
    }
    
    
    
    
    public static ArrayList<DateTime<Date, Time, String, String, Integer>> getResetSetArr()throws SQLException{
    		String sqlDate = "SELECT * FROM tblAlarm";
    		ResultSet rs = stmt.executeQuery(sqlDate);
    		ArrayList<DateTime<Date, Time, String, String, Integer>> arrList = new ArrayList<>();
    		
    		while (rs.next()){
    			arrList.add(new DateTime(rs.getDate("date"), rs.getTime("time"), rs.getString("subject"),
    					rs.getString("body"), rs.getInt("id")));
    		}
    		
    		return arrList;
    		
    }
    
    public static ArrayList<Date> getDistinctDate()throws SQLException{
    	String sqlDistinctDate = "SELECT DISTINCT date from tblAlarm";
    	ArrayList<Date> arrList = new ArrayList<>();
    	ResultSet rs = stmt.executeQuery(sqlDistinctDate);
		while (rs.next()){
			arrList.add(rs.getDate("date"));
		}
		
		return arrList;
    }
    
    
   
     
	public int insert(String subject, String body, String sound, Date date, String time)throws SQLException, ValueException{
	    	 if (subject == null || sound == null || date == null) throw new ValueException();
	    	   sqlPrepare = dbCon.prepareStatement("INSERT INTO tblAlarm (subject, body, sound, Date, Time) VALUES (?, ?, ?, ?, ?)");
	    	   sqlPrepare.setString(1, subject);
	    	   sqlPrepare.setString(2, body);
	    	   sqlPrepare.setString(3, sound);
	    	   sqlPrepare.setDate(4, new java.sql.Date(date.getTime())); //java.util.data is not the same as java.sql.date
	    	   sqlPrepare.setString(5, time);
	
	//     catch(ValueException ex){ //if we don't handle ValueException here, it will passed to its parent, and it parent and it parent ...
	//    	 ex.printStackTrace();
	//     }
	    int result = sqlPrepare.executeUpdate();
	    
	       if ( result == 0 ) 
	    	   throw new SQLException("Fail to insert value, please try again");
	       else 
	    	   return result; //0 for fail, 1 for success
	}
    
    public int getSize()throws SQLException{
    	String sqlCount = "SELECT COUNT(*) AS size FROM tblAlarm";
    	ResultSet rs = stmt.executeQuery(sqlCount);  
    	rs.next();
    	return rs.getInt("size");
    }
    
    
    public ResultSet getResultSet()throws SQLException{	
    	return stmt.executeQuery(sqlSelect);
    		
    }
    
    public int updateNote(String subject, String body, String sound, Date date, String time, String id)throws SQLException{
    	sqlPrepare = dbCon.prepareStatement("UPDATE tblAlarm SET subject = ?, body = ?, sound = ? , Date = ?, time = ? WHERE id = ?");
    	sqlPrepare.setString(1, subject);
 	   	sqlPrepare.setString(2, body);
 	   	sqlPrepare.setString(3, sound);
 	   	sqlPrepare.setDate(4, new java.sql.Date(date.getTime())); //java.util.data is not the same as java.sql.date
 	   	sqlPrepare.setString(5, time);
 	   	sqlPrepare.setString(6, id);
    	
 	   int result = sqlPrepare.executeUpdate();
	    
       if ( result == 0 ) 
    	   throw new SQLException("Fail to insert value, please try again");
       else 
    	   return result; //0 for fail, 1 for success

        
    }
    
    public ResultSet getResultSetOn(String date)throws SQLException{
    	String sqlGetOn = "SELECT * FROM tblAlarm WHERE Date = '" + date + "'";
    	return stmt.executeQuery(sqlGetOn);
    	 
    }
    
    public ResultSet getResultSetOfMonth(int month, int year)throws SQLException{
    	String sqlMonth = "SELECT date FROM tblAlarm WHERE MONTH(date) = " + month   + " AND YEAR(date) = " + year + " ORDER BY date ASC";
    	return stmt.executeQuery(sqlMonth);
    }
    
    public ResultSet getResultSetEdit(String id)throws SQLException{
    	String sqlEdit = "SELECT * FROM tblAlarm WHERE id = " + id + "";
    	
    	return stmt.executeQuery(sqlEdit);
    }
    
    public int deleteSQL(String id[])throws SQLException{
    	String sqlDelete = "DELETE FROM tblAlarm WHERE id IN (";
    	for (int i = 0; i < id.length; i++){
    		if (i == (id.length - 1)) 
    			sqlDelete += id[i];	
    		else
    			sqlDelete += id[i] + ',';
    	}
    	sqlDelete += ')';
    	return stmt.executeUpdate(sqlDelete); 
    }
    
    public int deleteAll()throws SQLException{
    	String sqlDeleteAll = "DELETE FROM tblAlarm";
    	return stmt.executeUpdate(sqlDeleteAll);
    }
    
    public String getDatabaseUserName(){
    	return userName;
    }
    
    public String getDatabasePassword(){
    	return passWord;
    }
     
    
    private boolean tableExist(Connection connection)throws SQLException{
    	boolean tblExist = false;	
    	DatabaseMetaData dbMeta = connection.getMetaData();
    	ResultSet rs = dbMeta.getTables(null, null, "%", null);    	
    	while (rs.next()) {   	 		   	 	  	  
    		if (rs.getString(3) == "TBLALARM") {
    			tblExist = true;  	 		 
    			System.out.print("in the tableExist");
    			break;
    		}
    	}    	
    	return tblExist;
    }
    
}
