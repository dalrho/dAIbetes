package org.example.daibetes.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteData{

    // Delete only if the report is not needed anymore
    public boolean deleteReport(int reportId) {
        String sql = "DELETE FROM tblReports WHERE report_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reportId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete diagnosis only if no report references it
    public boolean deleteDiagnosis(int diagnosisId) {
        String sql = "DELETE FROM tblDiagnosis WHERE diagnosis_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, diagnosisId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete detection only if no diagnosis/report references it
    public boolean deleteDetection(int detectionId) {
        String sql = "DELETE FROM tblDetection WHERE detection_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detectionId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
