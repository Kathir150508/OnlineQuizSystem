package gui;

import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Msg {

    public static void error(Component parent, String text) {
        JOptionPane.showMessageDialog(parent, text, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Oracle messages already start with the ORA- code. A short hint is added for the common setup mistakes.
    public static void error(Component parent, SQLException e) {
        String message = "Database error: " + e.getMessage();
        String text = String.valueOf(e.getMessage());
        if (text.contains("ORA-01017")) {
            message += "\n\nHint: wrong user or password. Check db.user and db.password in config.properties.";
        } else if (text.contains("ORA-12514") || text.contains("ORA-12541")) {
            message += "\n\nHint: Oracle is not reachable. Check that the database and listener are running "
                     + "and that db.url ends with /FREEPDB1.";
        } else if (text.contains("ORA-00942")) {
            message += "\n\nHint: a table is missing. Run schema_oracle.sql as the same user.";
        }
        error(parent, message);
    }

    public static void info(Component parent, String text) {
        JOptionPane.showMessageDialog(parent, text, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
