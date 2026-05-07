package org.example.daibetes.core.database;

import java.sql.*;

public class TestDAO {
    public int createTest(int patientId, int doctorId, int rawImgId) {
        String sql = """
        INSERT INTO tbltests (p_id, d_id, raw_img_id, tested_on)
        VALUES (?, ?, ?, NOW())
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setInt(3, rawImgId);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
