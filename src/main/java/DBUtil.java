import java.sql.*;

public class DBUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/users";     
    private static final String USER = "postgres"; 
    private static final String PASS = "sql@123";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
