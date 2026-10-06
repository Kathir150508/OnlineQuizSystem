package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseHelper {
    
    // Update these credentials to match your local Oracle database setup
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe"; 
    private static final String USER = "SYSTEM"; // Your Oracle username
    private static final String PASSWORD = "dharshini1411"; // Enter your Oracle password!

    public static Connection getConnection() throws SQLException {
        try {
            // This loads the Oracle JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found. Remember to add ojdbc.jar to your build path!");
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}