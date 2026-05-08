package org.example.daibetes.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteData{



    // Delete diagnosis only if no report references it
    public boolean deleteDiagnosis(int diagnosisId) {
        String sql = "DELETE FROM tbldiagnosis WHERE diagnosis_id = ?";

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
        String sql = "DELETE FROM tbldetection WHERE detection_id = ?";

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
