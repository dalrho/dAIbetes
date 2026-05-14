package org.example.daibetes.core.database;

import java.sql.*;

public class TestDAO {

    public int createTest(int patientId, int doctorId, int rawImageId) {
        String sql = """
            INSERT INTO tbltests (p_id, d_id, raw_img_id)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Create test failed: database connection is null.");
                return -1;
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, patientId);
                ps.setInt(2, doctorId);
                ps.setInt(3, rawImageId);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Creates a consultation request after a test is successfully inserted.
     * Delegates to ConsultationRequestDAO to keep responsibilities clean.
     *
     * @return generated request_id, or -1 on failure
     */
    public int createConsultationRequest(int testId, int patientId, int doctorId) {
        ConsultationRequestDAO requestDAO = new ConsultationRequestDAO();
        return requestDAO.createRequest(testId, patientId, doctorId);
    }
}