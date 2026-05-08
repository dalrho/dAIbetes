package org.example.daibetes.core.database;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ImageDAO {

    public int createImage(File imageFile, int imageTypeId) {
        String sql = """
        INSERT INTO tblimage (image_name, image_type_id, image_data)
        VALUES (?, ?, ?)
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
             FileInputStream fis = new FileInputStream(imageFile)) {

            ps.setString(1, imageFile.getName());
            ps.setInt(2, imageTypeId);
            ps.setBinaryStream(3, fis, (int) imageFile.length());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
