package org.example.daibetes.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DoctorDashboardDAO {

    public int getTotalReportsByDoctor(int doctorId) {
        String sql = """
            SELECT COUNT(*) AS total_reports
            FROM tblreport r
            INNER JOIN tbltests t
                ON r.test_id = t.test_id
            WHERE t.d_id = ?
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_reports");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void logOpenedReport(int doctorId, int reportId) {
        String sql = """
            INSERT INTO tbldoctoractivity
                (d_id, report_id, activity_text)
            SELECT
                t.d_id,
                r.report_id,
                CONCAT(
                    'Opened report #',
                    r.report_id,
                    ' for ',
                    u.firstname,
                    ' ',
                    u.lastname
                )
            FROM tblreport r
            INNER JOIN tbltests t
                ON r.test_id = t.test_id
            INNER JOIN tblpatient p
                ON t.p_id = p.p_id
            INNER JOIN tbluser u
                ON p.user_id = u.user_id
            WHERE r.report_id = ?
              AND t.d_id = ?
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reportId);
            ps.setInt(2, doctorId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getRecentActivitiesByDoctor(int doctorId) {
        List<String> activities = new ArrayList<>();

        String sql = """
            SELECT activity_text, opened_on
            FROM tbldoctoractivity
            WHERE d_id = ?
            ORDER BY opened_on DESC
            LIMIT 5
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String activity = rs.getString("activity_text");
                String openedOn = rs.getString("opened_on");

                activities.add(activity + " — " + openedOn);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return activities;
    }
}