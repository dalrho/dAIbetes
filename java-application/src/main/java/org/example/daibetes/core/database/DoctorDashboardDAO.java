package org.example.daibetes.core.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDashboardDAO {

    public String getDoctorName(int doctorId) {
        String sql = """
            SELECT u.firstname, u.lastname
            FROM tbldoctor d
            JOIN tbluser u ON d.user_id = u.user_id
            WHERE d.d_id = ?
        """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) return "Doctor";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, doctorId);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return rs.getString("firstname") + " " + rs.getString("lastname");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Doctor";
    }

    public int getTotalScans(int doctorId) {
        String sql = """
            SELECT COUNT(*) AS total_scans
            FROM tbltests
            WHERE d_id = ?
        """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) return 0;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, doctorId);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return rs.getInt("total_scans");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTestsWithoutDiagnosis(int doctorId) {
        String sql = """
            SELECT COUNT(*) AS to_review
            FROM tbldetection det
            JOIN tblfilteredscans fs
                ON det.filteredscan_id = fs.filteredscan_id
            JOIN tbltests t
                ON fs.test_id = t.test_id
            LEFT JOIN tbldiagnosis dg
                ON det.detection_id = dg.detection_id
            WHERE t.d_id = ?
              AND dg.diagnosis_id IS NULL
        """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) return 0;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, doctorId);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return rs.getInt("to_review");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<String> getRecentViewedPatients(int doctorId) {
        List<String> activities = new ArrayList<>();

        String sql = """
            SELECT 
                u.firstname,
                u.lastname,
                dg.diagnosis_text,
                dg.diagnosis_saved_on
            FROM tbldiagnosis dg
            JOIN tblpatient p
                ON dg.p_id = p.p_id
            JOIN tbluser u
                ON p.user_id = u.user_id
            WHERE dg.d_id = ?
            ORDER BY dg.diagnosis_saved_on DESC
            LIMIT 5
        """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) {
                activities.add("Database connection failed.");
                return activities;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, doctorId);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String patientName = rs.getString("firstname") + " " + rs.getString("lastname");
                    String savedOn = rs.getString("diagnosis_saved_on");

                    activities.add("Opened diagnosis for " + patientName + " - " + savedOn);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            activities.add("Unable to load recent activities.");
        }

        return activities;
    }
}