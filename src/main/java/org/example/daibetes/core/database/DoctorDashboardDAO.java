package org.example.daibetes.core.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDashboardDAO {

    public String getDoctorName(int doctorId) {
        String sql = """
            SELECT u.firstname, u.lastname
            FROM tblDoctor d
            JOIN tblUser u ON d.user_id = u.user_id
            WHERE d.d_id = ?
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("firstname") + " " + rs.getString("lastname");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Doctor";
    }

    public int getTotalScans(int doctorId) {
        String sql = "SELECT COUNT(*) FROM tblTests WHERE d_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTestsWithoutDiagnosis(int doctorId) {
        String sql = """
            SELECT COUNT(*)
            FROM tblTests t
            LEFT JOIN tblDiagnosis d ON t.test_id = d.test_id
            WHERE t.d_id = ? AND d.diagnosis_id IS NULL
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<String> getRecentViewedPatients(int doctorId) {
        List<String> activities = new ArrayList<>();

        String sql = """
            SELECT u.firstname, u.lastname, t.tested_on
            FROM tblTests t
            JOIN tblPatient p ON t.p_id = p.p_id
            JOIN tblUser u ON p.user_id = u.user_id
            WHERE t.d_id = ?
            ORDER BY t.tested_on DESC
            LIMIT 5
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                activities.add("Viewed patient: "
                        + rs.getString("firstname") + " "
                        + rs.getString("lastname")
                        + " - " + rs.getString("tested_on"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return activities;
    }
}