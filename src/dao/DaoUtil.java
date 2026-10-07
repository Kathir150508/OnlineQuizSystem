package dao;

import java.sql.*;
import util.DataBaseHelper;

class DaoUtil {

    static int count(String sql) throws SQLException {
        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
