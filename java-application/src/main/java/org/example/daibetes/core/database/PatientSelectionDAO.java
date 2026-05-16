package org.example.daibetes.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PatientSelectionDAO {

    public List<String[]> getAllPatients() {
        List<String[]> patients = new ArrayList<>();

        String sql = """
            SELECT 
                p.p_id,
                CONCAT(u.firstname, ' ', u.lastname) AS patient_name
            FROM tblpatient p
            JOIN tbluser u 
                ON p.user_id = u.user_id
            ORDER BY u.firstname, u.lastname
        """;

        try (Connection conn = MySQLConnection.getConnection()) {

            if (conn == null) {
                System.out.println("Database connection is null.");
                return patients;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    patients.add(new String[]{
                            String.valueOf(rs.getInt("p_id")),
                            rs.getString("patient_name")
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return patients;
    }
}