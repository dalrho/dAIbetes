package org.example.daibetes.core.database;

import org.example.daibetes.core.domain.Patient;
import org.example.daibetes.core.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RetrieveData {
    public User getUserByEmail(String email) {

        String sql = "SELECT * FROM tblUser WHERE email = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("contact_number"),
                        rs.getString("gender"),
                        rs.getString("birthdate")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Patient> getPatientsByDoctor(int doctorId) {
        List<Patient> patients = new ArrayList<>();

        String sql = """
        SELECT DISTINCT u.firstname, u.lastname, u.email,
                        u.contact_number, u.gender, u.birthdate,
                        p.age, p.p_id
        FROM tblPatient p
        JOIN tblUser u ON p.user_id = u.user_id
        JOIN tblTests t ON p.p_id = t.p_id
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
                        "", // password not needed
                        rs.getString("contact_number"),
                        rs.getString("gender"),
                        rs.getString("birthdate"),
                        rs.getInt("age")
                );

                patients.add(patient);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patients;
    }
}
