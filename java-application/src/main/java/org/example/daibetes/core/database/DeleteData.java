package org.example.daibetes.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteData {

    // Delete report and all its associated sub-records (cascading delete) in a transaction
    public boolean deleteReportCascade(int reportId) {
        String selectSql = "SELECT criticality_id, evaluation_id, findings_id, recommendations_id FROM tblreport WHERE report_id = ?";
        String deleteActivitySql = "DELETE FROM tbldoctoractivity WHERE report_id = ?";
        String deleteReportSql = "DELETE FROM tblreport WHERE report_id = ?";
        String deleteRecSql = "DELETE FROM tblrecommendations WHERE recommendation_id = ?";
        String deletePathSql = "DELETE FROM tblpathological WHERE findings_id = ?";
        String deleteFindSql = "DELETE FROM tblfindings WHERE evaluation_id = ?";
        String deleteCritSql = "DELETE FROM tblcriticality WHERE criticality_id = ?";

        Connection conn = null;
        try {
            conn = MySQLConnection.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            int criticalityId = -1;
            int evaluationId = -1;
            int findingsId = -1;
            int recommendationsId = -1;

            try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                psSelect.setInt(1, reportId);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        criticalityId = rs.getInt("criticality_id");
                        evaluationId = rs.getInt("evaluation_id");
                        findingsId = rs.getInt("findings_id");
                        recommendationsId = rs.getInt("recommendations_id");
                    } else {
                        // Report not found, nothing to do
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 1. Delete doctor activity logs first
            try (PreparedStatement ps = conn.prepareStatement(deleteActivitySql)) {
                ps.setInt(1, reportId);
                ps.executeUpdate();
            }

            // 2. Delete report itself
            try (PreparedStatement ps = conn.prepareStatement(deleteReportSql)) {
                ps.setInt(1, reportId);
                ps.executeUpdate();
            }

            // 3. Delete recommendations
            if (recommendationsId > 0) {
                try (PreparedStatement ps = conn.prepareStatement(deleteRecSql)) {
                    ps.setInt(1, recommendationsId);
                    ps.executeUpdate();
                }
            }

            // 4. Delete pathological findings
            if (findingsId > 0) {
                try (PreparedStatement ps = conn.prepareStatement(deletePathSql)) {
                    ps.setInt(1, findingsId);
                    ps.executeUpdate();
                }
            }

            // 5. Delete evaluation findings
            if (evaluationId > 0) {
                try (PreparedStatement ps = conn.prepareStatement(deleteFindSql)) {
                    ps.setInt(1, evaluationId);
                    ps.executeUpdate();
                }
            }

            // 6. Delete criticality
            if (criticalityId > 0) {
                try (PreparedStatement ps = conn.prepareStatement(deleteCritSql)) {
                    ps.setInt(1, criticalityId);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
