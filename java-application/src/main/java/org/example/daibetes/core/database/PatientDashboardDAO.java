package org.example.daibetes.core.database;

import org.example.daibetes.shared.models.Doctor;
import org.example.daibetes.shared.models.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDashboardDAO {

    // =========================================================================
    // 1. RECENT ACTIVITY PANEL
    // =========================================================================
    public List<Notification> getRecentActivities(int patientId) {
        List<Notification> activities = new ArrayList<>();

        String requestSql = """
            SELECT cr.request_id, cr.is_accepted, cr.responded_on,
                   CONCAT(u.firstname, ' ', u.lastname) AS doctor_name,
                   DATE_FORMAT(cr.requested_on, '%b %d, %Y') AS app_date
            FROM tblconsultationrequest cr
            JOIN tbldoctor d ON cr.d_id = d.d_id
            JOIN tbluser u ON d.user_id = u.user_id
            WHERE cr.p_id = ?
            ORDER BY cr.requested_on DESC LIMIT 5
        """;

        String diagnosisSql = """
            SELECT r.report_id, r.saved_on AS created_at, r.saved_on AS updated_at,
                   CONCAT(u.firstname, ' ', u.lastname) AS doctor_name
            FROM tblreport r
            JOIN tbltests t ON r.test_id = t.test_id
            JOIN tbldoctor d ON t.d_id = d.d_id
            JOIN tbluser u ON d.user_id = u.user_id
            WHERE t.p_id = ?
            ORDER BY r.saved_on DESC LIMIT 5
        """;

        try (Connection conn = MySQLConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(requestSql)) {
                ps.setInt(1, patientId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int reqId = rs.getInt("request_id");
                    String docName = rs.getString("doctor_name");
                    String dateStr = rs.getString("app_date");

                    Object isAcceptedObj = rs.getObject("is_accepted");
                    Timestamp responded = rs.getTimestamp("responded_on");

                    if (responded == null || isAcceptedObj == null) {
                        activities.add(new Notification(-1, patientId, reqId,
                                "Scheduled appointment still pending. Click here to view.",
                                "GO_TO_CALENDAR", false));
                    } else {
                        boolean accepted = rs.getBoolean("is_accepted");
                        if (accepted) {
                            activities.add(new Notification(-1, patientId, reqId,
                                    "Scheduled appointment accepted. Click here to see in calendar.",
                                    "GO_TO_CALENDAR", false));
                        } else {
                            activities.add(new Notification(-1, patientId, reqId,
                                    "Scheduled appointment rejected. Click here to reschedule.",
                                    "RESCHEDULE", false));
                        }
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(diagnosisSql)) {
                ps.setInt(1, patientId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String docName = rs.getString("doctor_name");
                    Timestamp created = rs.getTimestamp("created_at");
                    Timestamp updated = rs.getTimestamp("updated_at");

                    if (updated != null && created != null && (updated.getTime() - created.getTime() > 2000)) {
                        activities.add(new Notification(-1, patientId, -1,
                                "Dr. " + docName + " edited your diagnosis details. Click here to view records.",
                                "VIEW_DIAGNOSIS", false));
                    } else {
                        activities.add(new Notification(-1, patientId, -1,
                                "Your diagnosis from Dr. " + docName + " has been completed. Click here to view.",
                                "VIEW_DIAGNOSIS", false));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return activities;
    }

    // =========================================================================
    // 2. SCHEDULE & COUNTDOWN PANELS
    // =========================================================================
    public List<String[]> getConfirmedSchedules(int patientId) {
        List<String[]> results = new ArrayList<>();
        String sql = """
            SELECT CONCAT(u.firstname, ' ', u.lastname) AS doctor_name,
                   DATE_FORMAT(cr.requested_on, '%M %d, %Y %h:%i %p') AS formatted_datetime
            FROM tblconsultationrequest cr
            JOIN tbldoctor d ON cr.d_id = d.d_id
            JOIN tbluser u ON d.user_id = u.user_id
            WHERE cr.p_id = ? AND cr.is_accepted = 1 AND cr.requested_on >= NOW()
            ORDER BY cr.requested_on ASC
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new String[]{
                        rs.getString("doctor_name"),
                        rs.getString("formatted_datetime")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public String[] getNearestUpcomingAppointment(int patientId) {
        String sql = """
            SELECT CONCAT(u.firstname, ' ', u.lastname) AS doctor_name,
                   DATE_FORMAT(cr.requested_on, '%b %d, %Y') AS date_string,
                   DATEDIFF(cr.requested_on, NOW()) AS days_left
            FROM tblconsultationrequest cr
            JOIN tbldoctor d ON cr.d_id = d.d_id
            JOIN tbluser u ON d.user_id = u.user_id
            WHERE cr.p_id = ? AND cr.is_accepted = 1 AND cr.requested_on >= NOW()
            ORDER BY cr.requested_on ASC
            LIMIT 1
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                        rs.getString("doctor_name"),
                        rs.getString("date_string"),
                        String.valueOf(rs.getInt("days_left"))
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================================================================
    // 3. DOCTOR SEARCH & REQUEST FLOWS
    // =========================================================================
    public List<Doctor> searchDoctors(String keyword) {
        List<Doctor> results = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";

        String sql = """
            SELECT d.d_id, u.firstname, u.lastname, u.email,
                   u.contact_number, u.gender, u.birthdate,
                   d.license_number, d.hospital
            FROM tbldoctor d
            JOIN tbluser u ON d.user_id = u.user_id
            WHERE u.firstname LIKE ?
               OR u.lastname  LIKE ?
               OR d.hospital  LIKE ?
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Doctor doctor = new Doctor(
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("email"),
                        "",
                        rs.getString("contact_number"),
                        rs.getString("gender"),
                        rs.getString("birthdate"),
                        rs.getString("license_number"),
                        rs.getString("hospital"),
                        null
                );
                doctor.setDId(rs.getInt("d_id"));
                results.add(doctor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public boolean hasPendingRequest(int patientId, int doctorId) {
        String sql = """
            SELECT COUNT(*) FROM tbltests t
            LEFT JOIN tblreport r ON t.test_id = r.test_id
            WHERE t.p_id = ? AND t.d_id = ? AND r.report_id IS NULL
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int requestTest(int patientId, int doctorId, int rawImageId) {
        String sql = """
            INSERT INTO tbltests (p_id, d_id, raw_img_id, tested_on)
            VALUES (?, ?, ?, NOW())
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setInt(3, rawImageId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // =========================================================================
    // 4. HISTORIC DIAGNOSIS RECORDS LOGS
    // =========================================================================
    public List<String[]> getDiagnosesByPatient(int patientId) {
        List<String[]> results = new ArrayList<>();
        String sql = """
            SELECT r.report_id AS diagnosis_id,
                   CONCAT(u.firstname, ' ', u.lastname) AS doctor_name,
                   e.final_dr_grade AS diagnosis_text,
                   rec.final_notes AS recommendation,
                   DATE_FORMAT(r.saved_on, '%b %d, %Y') AS diagnosis_date
            FROM tblreport r
            JOIN tbltests t ON r.test_id = t.test_id
            JOIN tbldoctor doc ON t.d_id = doc.d_id
            JOIN tbluser   u   ON doc.user_id = u.user_id
            JOIN tblfindings e ON r.evaluation_id = e.evaluation_id
            JOIN tblrecommendations rec ON r.recommendations_id = rec.recommendation_id
            WHERE t.p_id = ?
            ORDER BY r.saved_on DESC
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                results.add(new String[]{
                        String.valueOf(rs.getInt("diagnosis_id")),
                        rs.getString("doctor_name"),
                        rs.getString("diagnosis_text"),
                        rs.getString("recommendation"),
                        rs.getString("diagnosis_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}