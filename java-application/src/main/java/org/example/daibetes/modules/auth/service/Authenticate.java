package org.example.daibetes.modules.auth.service;

import org.example.daibetes.core.database.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Authenticate {
    // Checks if email and password exist in tblUser
    public boolean login(String email, String password) {
        String sql = "SELECT * FROM tbluser WHERE email = ? AND password = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            // returns true if account exists
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Checks if email already exists before registration
    public boolean emailExists(String email) {
        String sql = "SELECT user_id FROM tbluser WHERE email = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

