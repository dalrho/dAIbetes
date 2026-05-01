package org.example.daibetes.core.database;

import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.Patient;
import org.example.daibetes.core.domain.User;

import java.sql.*;


public class CreateData {
    // Creates a new user account and returns the generated user_id
    public int createUser(User user) {

        String sql = """
        INSERT INTO tblUser 
        (firstname, lastname, email, password, contact_number, gender, birthdate)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Using getters from User class
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
                return rs.getInt(1); // return user_id
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

        String sql = "INSERT INTO tblPatient (user_id, age) VALUES (?, ?)";


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

        // 2 = doctor ID card (refer to the database structure tab in the docs file)
        ImageDAO imageDAO = new ImageDAO();

        int doctorIdCardId = imageDAO.createImage(
                doctor.getDoctorId(),
                2
        );

        if (doctorIdCardId == -1) {
            return false;
        }

        String sql = """
        INSERT INTO tbldoctor
        (user_id, doctor_idcard_id, license_number)
        VALUES (?, ?, ?)
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, doctorIdCardId);
            ps.setString(3, doctor.getLicenseNumber());
            ps.setString(4, doctor.getHospital());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
