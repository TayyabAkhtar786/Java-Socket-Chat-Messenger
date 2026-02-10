import java.sql.*;

public class DatabaseHelper {
   static Connection con; 
   public static Connection getConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_messenger", "root",  "root123" );
        }
        return con;
    }
}
