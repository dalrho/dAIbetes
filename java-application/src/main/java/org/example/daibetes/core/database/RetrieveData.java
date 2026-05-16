package org.example.daibetes.core.database;

import org.example.daibetes.shared.models.Doctor;
import org.example.daibetes.shared.models.Patient;
import org.example.daibetes.shared.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RetrieveData {


    public User getUserByEmail(String email) {

        // --- Try Doctor first ---
        String doctorSql = """
            SELECT u.user_id, u.firstname, u.lastname, u.email, u.password,
                   u.contact_number, u.gender, u.birthdate,
                   d.d_id, d.license_number, d.hospital, d.doctor_idcard_id
            FROM tbluser u
            JOIN tbldoctor d ON u.user_id = d.user_id
            WHERE u.email = ?
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(doctorSql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Doctor doctor = new Doctor(
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("contact_number"),
                        rs.getString("gender"),
                        rs.getString("birthdate"),
                        rs.getString("license_number"),
                        rs.getString("hospital"),
                        null // ID card image path resolved separately if needed
                );
                doctor.setDId(rs.getInt("d_id"));
                return doctor;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // --- Try Patient ---
        String patientSql = """
            SELECT u.user_id, u.firstname, u.lastname, u.email, u.password,
                   u.contact_number, u.gender, u.birthdate,
                   p.p_id, p.age
            FROM tbluser u
            JOIN tblpatient p ON u.user_id = p.user_id
            WHERE u.email = ?
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(patientSql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Patient patient = new Patient(
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("contact_number"),
                        rs.getString("gender"),
                        rs.getString("birthdate"),
                        rs.getInt("age")
                );
                patient.setPId(rs.getInt("p_id"));
                return patient;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // email not found in either role table
    }

    public List<Patient> getPatientsByDoctor(int doctorId) {
        List<Patient> patients = new ArrayList<>();

        String sql = """
            SELECT DISTINCT u.firstname, u.lastname, u.email,
                            u.contact_number, u.gender, u.birthdate,
                            p.age, p.p_id
            FROM tblpatient p
            JOIN tbluser u ON p.user_id = u.user_id
            JOIN tbltests t ON p.p_id = t.p_id
            WHERE t.d_id = ?
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Patient patient = new Patient(
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("email"),
                        "",
                        rs.getString("contact_number"),
                        rs.getString("gender"),
                        rs.getString("birthdate"),
                        rs.getInt("age")
                );
                patient.setPId(rs.getInt("p_id"));
                patients.add(patient);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patients;
    }
}