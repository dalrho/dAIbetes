package org.example.daibetes.core.database;

import org.example.daibetes.modules.doctor.ui.review.model.ReportData;
import org.example.daibetes.shared.models.Doctor;
import org.example.daibetes.shared.models.Notification;
import javafx.scene.image.Image;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDashboardDAO {

    // =========================================================================
    // 1. RECENT ACTIVITY PANEL
    // =========================================================================
    public List<Notification> getRecentActivities(int patientId) {
        List<Notification> activities = new ArrayList<>();
        String requestSql = "SELECT cr.request_id, cr.is_accepted, cr.responded_on FROM tblconsultationrequest cr WHERE cr.p_id = ? ORDER BY cr.requested_on DESC LIMIT 5";
        String reportSql = "SELECT r.report_id, r.saved_on, CONCAT(u.firstname, ' ', u.lastname) AS doctor_name " +
                "FROM tblreport r JOIN tbltests t ON r.test_id = t.test_id " +
                "JOIN tbldoctor d ON t.d_id = d.d_id JOIN tbluser u ON d.user_id = u.user_id " +
                "WHERE t.p_id = ? ORDER BY r.saved_on DESC LIMIT 5";

        try (Connection conn = MySQLConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(requestSql)) {
                ps.setInt(1, patientId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    activities.add(new Notification(-1, patientId, rs.getInt("request_id"), "Appointment status updated.", "GO_TO_CALENDAR", false));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(reportSql)) {
                ps.setInt(1, patientId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    activities.add(new Notification(-1, patientId, -1, "New diagnosis from Dr. " + rs.getString("doctor_name") + " is ready.", "VIEW_DIAGNOSIS", false));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return activities;
    }

    // =========================================================================
    // 2. SCHEDULE & COUNTDOWN PANELS
    // =========================================================================
    public List<String[]> getConfirmedSchedules(int patientId) {
        List<String[]> results = new ArrayList<>();
        String sql = "SELECT CONCAT(u.firstname, ' ', u.lastname) AS doctor_name, DATE_FORMAT(cr.requested_on, '%M %d, %Y %h:%i %p') AS formatted_datetime " +
                "FROM tblconsultationrequest cr JOIN tbldoctor d ON cr.d_id = d.d_id JOIN tbluser u ON d.user_id = u.user_id " +
                "WHERE cr.p_id = ? AND cr.is_accepted = 1 AND cr.requested_on >= NOW() ORDER BY cr.requested_on ASC";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) results.add(new String[]{rs.getString("doctor_name"), rs.getString("formatted_datetime")});
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }

    public String[] getNearestUpcomingAppointment(int patientId) {
        String sql = "SELECT CONCAT(u.firstname, ' ', u.lastname) AS doctor_name, DATE_FORMAT(cr.requested_on, '%b %d, %Y') AS date_string, DATEDIFF(cr.requested_on, NOW()) AS days_left " +
                "FROM tblconsultationrequest cr JOIN tbldoctor d ON cr.d_id = d.d_id JOIN tbluser u ON d.user_id = u.user_id " +
                "WHERE cr.p_id = ? AND cr.is_accepted = 1 AND cr.requested_on >= NOW() ORDER BY cr.requested_on ASC LIMIT 1";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new String[]{rs.getString("doctor_name"), rs.getString("date_string"), String.valueOf(rs.getInt("days_left"))};
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // =========================================================================
    // 3. DOCTOR SEARCH & REQUEST FLOWS
    // =========================================================================

    public List<Doctor> searchDoctors(String keyword) {
        List<Doctor> results = new ArrayList<>();
        String sql = "SELECT d.d_id, u.firstname, u.lastname, u.email, u.contact_number, u.gender, u.birthdate, d.license_number, d.hospital " +
                "FROM tbldoctor d JOIN tbluser u ON d.user_id = u.user_id WHERE u.firstname LIKE ? OR u.lastname LIKE ? OR d.hospital LIKE ?";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%"; ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Doctor doc = new Doctor(rs.getString("firstname"), rs.getString("lastname"), rs.getString("email"), "", rs.getString("contact_number"), rs.getString("gender"), rs.getString("birthdate"), rs.getString("license_number"), rs.getString("hospital"), null);
                doc.setDId(rs.getInt("d_id")); results.add(doc);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }

    public boolean hasPendingRequest(int patientId, int doctorId) {
        String sql = "SELECT COUNT(*) FROM tbltests t LEFT JOIN tblreport r ON t.test_id = r.test_id WHERE t.p_id = ? AND t.d_id = ? AND r.report_id IS NULL";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId); ps.setInt(2, doctorId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public int requestTest(int patientId, int doctorId, int rawImageId) {
        String sql = "INSERT INTO tbltests (p_id, d_id, raw_img_id, tested_on) VALUES (?, ?, ?, NOW())";
        try (Connection conn = MySQLConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, patientId); ps.setInt(2, doctorId); ps.setInt(3, rawImageId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    // =========================================================================
    // 4. HISTORIC DIAGNOSIS SUMMARY (For the Table)
    // =========================================================================
    public List<String[]> getDiagnosesByPatient(int patientId) {
        List<String[]> list = new ArrayList<>();
        // Added p.`8` to get the actual Diagnosis Grade
        String sql = """
        SELECT r.report_id, 
               CONCAT(u.firstname, ' ', u.lastname) AS doc_name, 
               c.criticality_lvl, 
               p.`8` AS dr_grade, 
               r.saved_on 
        FROM tblreport r 
        JOIN tbltests t ON r.test_id = t.test_id 
        JOIN tbldoctor d ON t.d_id = d.d_id 
        JOIN tbluser u ON d.user_id = u.user_id 
        JOIN tblcriticality c ON r.criticality_id = c.criticality_id 
        JOIN tblpathological p ON r.findings_id = p.findings_id
        WHERE t.p_id = ? 
        ORDER BY r.saved_on DESC""";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("report_id")), // row[0]
                        rs.getString("doc_name"),               // row[1]
                        rs.getString("criticality_lvl"),        // row[2]
                        rs.getString("dr_grade"),               // row[3]
                        rs.getString("saved_on")                // row[4]
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    public ReportData getFullReportDetails(int reportId) {
        ReportData data = new ReportData();
        String sql = """
    SELECT r.report_id, 
           CONCAT(up.firstname, ' ', up.lastname) AS patient_name,
           CONCAT(ud.firstname, ' ', ud.lastname) AS attending_physician, 
           c.criticality_lvl, c.reasoning, r.saved_on, 
           p.`1` as col1, p.`2` as col2, p.`3` as col3, p.`4` as col4, p.`5` as col5, 
           p.`6` as col6, p.`7` as col7, p.`8` as col8, p.`9` as col9, p.`10` as col10,
           rec.isAnnual, rec.isSixMonth, rec.isRefer, rec.isUrgent, rec.isLaser, rec.isVEGF, rec.final_notes,
           img.image_data 
    FROM tblreport r 
    JOIN tbltests t ON r.test_id = t.test_id 
    JOIN tblpatient pat ON t.p_id = pat.p_id
    JOIN tbluser up ON pat.user_id = up.user_id
    JOIN tbldoctor d ON t.d_id = d.d_id 
    JOIN tbluser ud ON d.user_id = ud.user_id 
    JOIN tblcriticality c ON r.criticality_id = c.criticality_id 
    JOIN tblpathological p ON r.findings_id = p.findings_id 
    JOIN tblrecommendations rec ON r.recommendations_id = rec.recommendation_id 
    LEFT JOIN tblimage img ON t.raw_img_id = img.img_id
    WHERE r.report_id = ?""";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                data.setReportId(rs.getInt("report_id"));
                data.setPatientName(rs.getString("patient_name")); // The actual patient
                data.setDoctorName(rs.getString("attending_physician")); // The actual doctor

                data.setCriticality(rs.getString("criticality_lvl"));
                data.setCriticalityReasoning(rs.getString("reasoning"));
                data.setClinicalNotes(rs.getString("final_notes"));

                // Findings 1-10 mapping (Shortened for brevity)
                data.setMicroaneurysms(rs.getString("col1"));
                data.setHemorrhages(rs.getString("col2"));
                data.setHardExudates(rs.getString("col3"));
                data.setCottonWoolSpots(rs.getString("col4"));
                data.setMacularEdema(rs.getString("col5"));
                data.setVenousBeading(rs.getString("col6"));
                data.setIrma(rs.getString("col7"));
                data.setNeovascularization(rs.getString("col8"));
                data.setVitreousHemorrhage(rs.getString("col9"));
                data.setRetinalDetachment(rs.getString("col10"));

                data.setDrGrade(rs.getString("col8"));
                data.setDmeGrade(rs.getString("col9"));

                Blob blob = rs.getBlob("image_data");
                if (blob != null) data.setScanImage(new javafx.scene.image.Image(blob.getBinaryStream()));
                List<String> recs = new ArrayList<>();

                if (rs.getInt("isAnnual") == 1)   recs.add("Annual Screening");
                if (rs.getInt("isSixMonth") == 1) recs.add("6-Month Follow-up");
                if (rs.getInt("isRefer") == 1)    recs.add("Specialist Referral");
                if (rs.getInt("isUrgent") == 1)   recs.add("Urgent Attention");
                if (rs.getInt("isLaser") == 1)    recs.add("Laser Surgery");
                if (rs.getInt("isVEGF") == 1)     recs.add("Anti-VEGF Injection");

                data.setRecommendations(recs);
                data.setClinicalNotes(rs.getString("final_notes"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }
}