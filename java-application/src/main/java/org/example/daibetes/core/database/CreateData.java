package org.example.daibetes.core.database;

import org.example.daibetes.shared.models.Doctor;
import org.example.daibetes.shared.models.Patient;
import org.example.daibetes.shared.models.User;

import java.sql.*;

//USABLE FOR REGISTER
public class CreateData {
    // Creates a new user account and returns the generated user_id
    public int createUser(User user) {
        String sql = """
        INSERT INTO tbluser 
        (firstname, lastname, email, password, contact_number, gender, birthdate)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = MySQLConnection.getConnection()) {

            if (conn == null) {
                System.out.println("Create user failed: database connection is null.");
                return -1;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getFirstname());
                ps.setString(2, user.getLastname());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPassword());
                ps.setString(5, user.getContactNumber());
                ps.setString(6, user.getGender());
                ps.setString(7, user.getBirthdate());

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean createPatient(Patient patient) {
        int userId = createUser(patient);

        if (userId == -1) {
            return false;
        }

        String sql = "INSERT INTO tblpatient (user_id, age) VALUES (?, ?)";


        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, patient.getAge());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createDoctor(Doctor doctor) {
        int userId = createUser(doctor);

        if (userId == -1) {
            return false;
        }

        String sql = """
        INSERT INTO tbldoctor
        (user_id, license_number, hospital)
        VALUES (?, ?, ?)
    """;

        try (Connection conn = MySQLConnection.getConnection()) {

            if (conn == null) {
                System.out.println("Create doctor failed: database connection is null.");
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setString(2, doctor.getLicenseNumber());
                ps.setString(3, doctor.getHospital());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
