package org.example.daibetes.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteData {

    // Delete report only if no foreign key constraints are violated
    public boolean deleteDiagnosis(int reportId) {
        String sql = "DELETE FROM tblreport WHERE report_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reportId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete test only if no foreign key constraints are violated
    public boolean deleteDetection(int testId) {
        String sql = "DELETE FROM tbltests WHERE test_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, testId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
