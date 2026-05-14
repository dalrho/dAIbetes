package org.example.daibetes.core.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for tblConsultationRequest.
 * Handles fetching pending requests and accepting/rejecting them.
 *
 * responded_on = NULL  → still pending
 * responded_on = NOW() → doctor has responded (accepted or rejected)
 * is_accepted  = true  → accepted
 * is_accepted  = false + responded_on NOT NULL → rejected
 */
public class ConsultationRequestDAO {

    // =========================================================================
    // FETCH — pending requests for a doctor
    // =========================================================================

    /**
     * Returns all pending (unresponded) consultation requests for a doctor.
     * Each row: [0]=request_id [1]=test_id [2]=patient_name
     *           [3]=requested_on [4]=p_id
     */
    public List<String[]> getPendingRequests(int doctorId) {
        List<String[]> results = new ArrayList<>();

        String sql = """
            SELECT cr.request_id,
                   cr.test_id,
                   CONCAT(u.firstname, ' ', u.lastname) AS patient_name,
                   DATE_FORMAT(cr.requested_on, '%b %d, %Y %h:%i %p') AS requested_on,
                   cr.p_id
            FROM tblConsultationRequest cr
            JOIN tblPatient pat ON cr.p_id  = pat.p_id
            JOIN tblUser    u   ON pat.user_id = u.user_id
            WHERE cr.d_id = ?
              AND cr.responded_on IS NULL
            ORDER BY cr.requested_on DESC
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                results.add(new String[]{
                        String.valueOf(rs.getInt("request_id")),
                        String.valueOf(rs.getInt("test_id")),
                        rs.getString("patient_name"),
                        rs.getString("requested_on"),
                        String.valueOf(rs.getInt("p_id"))
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    // =========================================================================
    // ACCEPT
    // =========================================================================

    /**
     * Marks a consultation request as accepted.
     * Sets is_accepted = true and responded_on = NOW().
     */
    public boolean acceptRequest(int requestId) {
        String sql = """
            UPDATE tblConsultationRequest
            SET is_accepted  = true,
                responded_on = NOW()
            WHERE request_id = ?
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================================================================
    // REJECT
    // =========================================================================

    /**
     * Marks a consultation request as rejected.
     * Sets is_accepted = false and responded_on = NOW().
     */
    public boolean rejectRequest(int requestId) {
        String sql = """
            UPDATE tblConsultationRequest
            SET is_accepted  = false,
                responded_on = NOW()
            WHERE request_id = ?
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================================================================
    // FETCH — responded (archived) requests for a doctor
    // =========================================================================

    /**
     * Returns all responded consultation requests (accepted or rejected).
     * responded_on IS NOT NULL means the doctor has already acted on it.
     * Each row: [0]=request_id [1]=test_id [2]=patient_name
     *           [3]=requested_on [4]=p_id
     */
    public List<String[]> getRespondedRequests(int doctorId) {
        List<String[]> results = new ArrayList<>();

        String sql = """
            SELECT cr.request_id,
                   cr.test_id,
                   CONCAT(u.firstname, ' ', u.lastname) AS patient_name,
                   DATE_FORMAT(cr.requested_on, '%b %d, %Y %h:%i %p') AS requested_on,
                   cr.p_id
            FROM tblConsultationRequest cr
            JOIN tblPatient pat ON cr.p_id    = pat.p_id
            JOIN tblUser    u   ON pat.user_id = u.user_id
            WHERE cr.d_id = ?
              AND cr.responded_on IS NOT NULL
            ORDER BY cr.responded_on DESC
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                results.add(new String[]{
                        String.valueOf(rs.getInt("request_id")),
                        String.valueOf(rs.getInt("test_id")),
                        rs.getString("patient_name"),
                        rs.getString("requested_on"),
                        String.valueOf(rs.getInt("p_id"))
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    // =========================================================================
    // CREATE — called when patient submits a request
    // =========================================================================

    /**
     * Inserts a new consultation request row.
     * responded_on is left NULL (pending).
     *
     * @return generated request_id, or -1 on failure
     */
    public int createRequest(int testId, int patientId, int doctorId) {
        String sql = """
            INSERT INTO tblConsultationRequest
                (test_id, p_id, d_id, is_accepted, requested_on)
            VALUES (?, ?, ?, false, NOW())
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, testId);
            ps.setInt(2, patientId);
            ps.setInt(3, doctorId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // =========================================================================
    // COUNT — used by dashboard summary card
    // =========================================================================

    /**
     * Returns total pending request count for a doctor.
     * Used to populate the "To Review" label on the dashboard.
     */
    public int getPendingRequestCount(int doctorId) {
        String sql = """
            SELECT COUNT(*) FROM tblConsultationRequest
            WHERE d_id = ? AND responded_on IS NULL
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
}