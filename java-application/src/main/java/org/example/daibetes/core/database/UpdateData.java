package org.example.daibetes.core.database;

import org.example.daibetes.shared.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateData {
    public boolean updateUser(int userId, User user) {

        String sql = """
        UPDATE tbluser
        SET firstname = ?, lastname = ?, contact_number = ?, gender = ?
        WHERE user_id = ?
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFirstname());
            ps.setString(2, user.getLastname());
            ps.setString(3, user.getContactNumber());
            ps.setString(4, user.getGender());
            ps.setInt(5, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
