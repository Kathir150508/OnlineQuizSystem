package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseHelper {

    private static final String DRIVER = "oracle.jdbc.OracleDriver";
    private static final Properties props = new Properties();

    static {
        try (FileInputStream in = new FileInputStream("config.properties")) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("Could not read config.properties: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC driver not found. Put ojdbc17.jar in the lib folder "
                + "and add it to the build path.");
        }
        String url = props.getProperty("db.url");
        if (url == null || url.trim().isEmpty()) {
            throw new SQLException("db.url is missing. Check that config.properties is in the folder "
                + "you start the program from.");
        }
        return DriverManager.getConnection(
            url,
            props.getProperty("db.user"),
            props.getProperty("db.password"));
    }

    public static String getAdminPin() {
        return props.getProperty("admin.pin", "1234");
    }
}
