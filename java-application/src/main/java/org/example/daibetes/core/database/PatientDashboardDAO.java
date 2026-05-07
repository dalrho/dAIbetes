package org.example.daibetes.core.database;

import org.example.daibetes.core.domain.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for all Patient Dashboard queries.
 * Covers: Search (My Doctors), View (Diagnosis), Request, and Schedule.
 */
public class PatientDashboardDAO {

    // =========================================================================
    // SEARCH — doctors linked to this patient via tblTests
    // =========================================================================

    /**
     * Returns all doctors that have ever handled a test for this patient.
     * Used to populate "My Doctors".
     */
    public List<Doctor> getDoctorsByPatient(int patientId) {
        List<Doctor> results = new ArrayList<>();

        String sql = """
            SELECT DISTINCT d.d_id, u.firstname, u.lastname, u.email,
                            u.contact_number, u.gender, u.birthdate,
                            d.license_number, d.hospital
            FROM tblTests t
            JOIN tblDoctor d ON t.d_id     = d.d_id
            JOIN tblUser   u ON d.user_id  = u.user_id
            WHERE t.p_id = ?
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
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

    /**
     * Full doctor search by keyword against name or hospital.
     * Used when requesting a scan from a new doctor.
     */
    public List<Doctor> searchDoctors(String keyword) {
        List<Doctor> results = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";

        String sql = """
            SELECT d.d_id, u.firstname, u.lastname, u.email,
                   u.contact_number, u.gender, u.birthdate,
                   d.license_number, d.hospital
            FROM tblDoctor d
            JOIN tblUser u ON d.user_id = u.user_id
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

    // =========================================================================
    // VIEW — diagnosis records
    // =========================================================================

    /**
     * Returns all diagnosis records for a patient, newest first.
     * Columns: [0]=diagnosis_id [1]=doctor_name [2]=diagnosis_text
     *          [3]=recommendation [4]=diagnosis_date
     */
    public List<String[]> getDiagnosesByPatient(int patientId) {
        List<String[]> results = new ArrayList<>();

        String sql = """
            SELECT diag.diagnosis_id,
                   CONCAT(u.firstname, ' ', u.lastname) AS doctor_name,
                   diag.diagnosis_text,
                   diag.recommendation,
                   DATE_FORMAT(diag.diagnosis_date, '%b %d, %Y') AS diagnosis_date
            FROM tblDiagnosis diag
            JOIN tblDoctor doc ON diag.d_id   = doc.d_id
            JOIN tblUser   u   ON doc.user_id  = u.user_id
            WHERE diag.p_id = ?
            ORDER BY diag.diagnosis_date DESC
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

    // =========================================================================
    // REQUEST — submit a new scan test
    // =========================================================================

    /**
     * Inserts a new row into tblTests.
     * @return generated test_id, or -1 on failure.
     */
    public int requestTest(int patientId, int doctorId, int rawImageId) {
        String sql = """
            INSERT INTO tblTests (p_id, d_id, raw_img_id, tested_on)
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

    /**
     * Prevents duplicate pending requests to the same doctor.
     */
    public boolean hasPendingRequest(int patientId, int doctorId) {
        String sql = """
            SELECT COUNT(*) FROM tblTests t
            LEFT JOIN tblDiagnosis d ON t.test_id = d.detection_id
            WHERE t.p_id = ? AND t.d_id = ? AND d.diagnosis_id IS NULL
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

    // =========================================================================
    // SCHEDULE — upcoming / past tests
    // =========================================================================

    /**
     * Returns all test records for a patient, newest first.
     * Columns: [0]=test_id [1]=doctor_name [2]=tested_on [3]=status
     */
    public List<String[]> getScheduleByPatient(int patientId) {
        List<String[]> results = new ArrayList<>();

        String sql = """
            SELECT t.test_id,
                   CONCAT(u.firstname, ' ', u.lastname) AS doctor_name,
                   DATE_FORMAT(t.tested_on, '%b %d, %Y') AS tested_on,
                   CASE WHEN d.diagnosis_id IS NOT NULL THEN 'Completed'
                        ELSE 'Pending'
                   END AS status
            FROM tblTests t
            JOIN tblDoctor    doc ON t.d_id     = doc.d_id
            JOIN tblUser      u   ON doc.user_id = u.user_id
            LEFT JOIN tblDiagnosis d ON t.test_id = d.detection_id
                                     AND d.p_id   = t.p_id
            WHERE t.p_id = ?
            ORDER BY t.tested_on DESC
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                results.add(new String[]{
                        String.valueOf(rs.getInt("test_id")),
                        rs.getString("doctor_name"),
                        rs.getString("tested_on"),
                        rs.getString("status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Returns the next upcoming (pending) test for the patient.
     * Used by the "Days Until Follow-up" card.
     * Columns: [0]=doctor_name [1]=tested_on
     */
    public String[] getNextSchedule(int patientId) {
        String sql = """
            SELECT CONCAT(u.firstname, ' ', u.lastname) AS doctor_name,
                   DATE_FORMAT(t.tested_on, '%b %d, %Y')  AS tested_on
            FROM tblTests t
            JOIN tblDoctor    doc ON t.d_id     = doc.d_id
            JOIN tblUser      u   ON doc.user_id = u.user_id
            LEFT JOIN tblDiagnosis d ON t.test_id = d.detection_id
                                     AND d.p_id   = t.p_id
            WHERE t.p_id = ? AND d.diagnosis_id IS NULL
            ORDER BY t.tested_on ASC
            LIMIT 1
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new String[]{
                        rs.getString("doctor_name"),
                        rs.getString("tested_on")
                };
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}