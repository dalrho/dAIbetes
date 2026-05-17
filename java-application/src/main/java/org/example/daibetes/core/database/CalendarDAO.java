package org.example.daibetes.core.database;

import org.example.daibetes.shared.models.Appointment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for the Patient Calendar screen.
 * Covers:
 *   - Fetching all doctors for the ComboBox
 *   - Fetching a patient's appointments from tblConsultationRequest
 *   - Inserting a new appointment request into tblConsultationRequest
 */
public class CalendarDAO implements ICalendarDAO {

    // =========================================================================
    // DOCTORS — for the ComboBox
    // =========================================================================

    /**
     * Returns all doctors as "Dr. Firstname Lastname" strings for the ComboBox.
     * Also returns their d_id so we can map selection back to an ID.
     * Format: String[0] = d_id, String[1] = display name
     */
    @Override
    public List<String[]> getAllDoctors() {
        List<String[]> results = new ArrayList<>();

        String sql = """
            SELECT d.d_id,
                   CONCAT('Dr. ', u.firstname, ' ', u.lastname) AS display_name,
                   d.hospital
            FROM tbldoctor d
            JOIN tbluser u ON d.user_id = u.user_id
            ORDER BY u.lastname ASC
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new String[]{
                        String.valueOf(rs.getInt("d_id")),
                        rs.getString("display_name"),
                        rs.getString("hospital")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    // =========================================================================
    // APPOINTMENTS — fetch for a patient
    // =========================================================================

    /**
     * Fetches all consultation requests for a patient as Appointment objects.
     * Status is derived from is_accepted + responded_on:
     *   responded_on IS NULL                       → PENDING
     *   responded_on IS NOT NULL + is_accepted=1   → ACCEPTED
     *   responded_on IS NOT NULL + is_accepted=0   → REJECTED
     *
     * requested_on is used as the appointment date.
     * time is stored in AppContext as a separate field since tblConsultationRequest
     * has no time column — it is stored via the notes approach below.
     */
    @Override
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        List<Appointment> results = new ArrayList<>();

        String sql = """
            SELECT cr.request_id,
                   cr.p_id,
                   cr.d_id,
                   CONCAT(u.firstname, ' ', u.lastname) AS patient_name,
                   CONCAT('Dr. ', du.firstname, ' ', du.lastname) AS doctor_name,
                   DATE(cr.requested_on)   AS appt_date,
                   TIME_FORMAT(TIME(cr.requested_on), '%h:%i %p') AS appt_time,
                   cr.is_accepted,
                   cr.responded_on
            FROM tblconsultationrequest cr
            JOIN tblpatient pat ON cr.p_id    = pat.p_id
            JOIN tbluser    u   ON pat.user_id = u.user_id
            JOIN tbldoctor  doc ON cr.d_id     = doc.d_id
            JOIN tbluser    du  ON doc.user_id  = du.user_id
            WHERE cr.p_id = ?
            ORDER BY cr.requested_on DESC
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Appointment.Status status;

                if (rs.getTimestamp("responded_on") == null) {
                    status = Appointment.Status.PENDING;
                } else if (rs.getBoolean("is_accepted")) {
                    status = Appointment.Status.ACCEPTED;
                } else {
                    status = Appointment.Status.REJECTED;
                }

                LocalDate date = rs.getDate("appt_date").toLocalDate();

                results.add(new Appointment(
                        rs.getInt("request_id"),
                        rs.getInt("p_id"),
                        rs.getInt("d_id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        date,
                        rs.getString("appt_time"),
                        status
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }


    /**
     * Fetches all consultation requests for a doctor as Appointment objects.
     */
    @Override
    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        List<Appointment> results = new ArrayList<>();

        String sql = """
            SELECT cr.request_id,
                   cr.p_id,
                   cr.d_id,
                   CONCAT(u.firstname, ' ', u.lastname) AS patient_name,
                   CONCAT('Dr. ', du.firstname, ' ', du.lastname) AS doctor_name,
                   DATE(cr.requested_on)   AS appt_date,
                   TIME_FORMAT(TIME(cr.requested_on), '%h:%i %p') AS appt_time,
                   cr.is_accepted,
                   cr.responded_on
            FROM tblconsultationrequest cr
            JOIN tblpatient pat ON cr.p_id    = pat.p_id
            JOIN tbluser    u   ON pat.user_id = u.user_id
            JOIN tbldoctor  doc ON cr.d_id     = doc.d_id
            JOIN tbluser    du  ON doc.user_id  = du.user_id
            WHERE cr.d_id = ?
            ORDER BY cr.requested_on DESC
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Appointment.Status status;

                if (rs.getTimestamp("responded_on") == null) {
                    status = Appointment.Status.PENDING;
                } else if (rs.getBoolean("is_accepted")) {
                    status = Appointment.Status.ACCEPTED;
                } else {
                    status = Appointment.Status.REJECTED;
                }

                LocalDate date = rs.getDate("appt_date").toLocalDate();

                results.add(new Appointment(
                        rs.getInt("request_id"),
                        rs.getInt("p_id"),
                        rs.getInt("d_id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        date,
                        rs.getString("appt_time"),
                        status
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Updates appointment status in the database.
     */
    @Override
    public boolean updateAppointmentStatus(int requestId, boolean accepted) {
        String sql = """
            UPDATE tblconsultationrequest
            SET is_accepted  = ?,
                responded_on = NOW()
            WHERE request_id = ?
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, accepted);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================================================================
    // INSERT — new appointment request
    // =========================================================================

    /**
     * Inserts a new consultation request.
     * Since tblConsultationRequest links through tblTests (test_id FK),
     * a test row is created first via TestDAO, then the request is inserted.
     *
     * The preferred time from the UI is encoded into the requested_on datetime:
     * the date comes from requestDatePicker and the time from timeField.
     *
     * @return generated request_id, or -1 on failure
     */
    @Override
    public int insertAppointmentRequest(int patientId, int doctorId,
                                        String requestedOn) {
        // Step 1: create a test row with no image yet (raw_img_id = NULL)
        // The doctor will attach the actual scan during the diagnosis step.
        TestDAO testDAO = new TestDAO();
        int testId = testDAO.createTest(patientId, doctorId, -1);

        if (testId == -1) {
            System.err.println("CalendarDAO: failed to create test row.");
            return -1;
        }

        // Step 2: insert consultation request with the patient's chosen datetime
        String sql = """
            INSERT INTO tblconsultationrequest
                (test_id, p_id, d_id, is_accepted, requested_on)
            VALUES (?, ?, ?, 0, ?)
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, testId);
            ps.setInt(2, patientId);
            ps.setInt(3, doctorId);
            ps.setString(4, requestedOn); // "YYYY-MM-DD HH:MM:SS"

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }


    // =========================================================================
    // NEW: REMOVE — Delete rejected request
    // =========================================================================

    /**
     * Removes an appointment request from the database.
     * Typically used by patients to clear rejected appointments from view.
     */
    @Override
    public boolean deleteAppointmentRequest(int requestId) {
        String sql = "DELETE FROM tblconsultationrequest WHERE request_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Integer save(Appointment entity) {
        return 0;
    }

    @Override
    public Optional<Appointment> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public List<Appointment> findAll() {
        return List.of();
    }

    @Override
    public boolean update(Appointment entity) {
        return false;
    }

    @Override
    public boolean deleteById(Integer integer) {
        return false;
    }
}