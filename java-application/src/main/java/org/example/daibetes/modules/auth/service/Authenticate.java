package org.example.daibetes.modules.auth.service;

import org.example.daibetes.core.database.MySQLConnection;
import org.example.daibetes.shared.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Authenticate {

    public boolean login(String email, String password) {
        String sql = "SELECT password FROM tbluser WHERE email = ?";

        try (Connection conn = MySQLConnection.getConnection()) {

            if (conn == null) {
                System.out.println("Login failed: database connection is null.");
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    return PasswordUtils.checkPassword(password, hashedPassword);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT user_id FROM tbluser WHERE email = ?";

        try (Connection conn = MySQLConnection.getConnection()) {

            if (conn == null) {
                System.out.println("Email check failed: database connection is null.");
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);

                ResultSet rs = ps.executeQuery();
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}