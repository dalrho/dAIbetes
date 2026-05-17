package org.example.daibetes.shared.service;

import org.example.daibetes.core.database.MySQLConnection;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReportGenerationService implements IReportGenerationService {

    @Override
    public int generateAndPersistReport(
            int patientId,
            int doctorId,
            File reportImageFile,
            String criticality,
            String doctorReasoning,
            String microaneurysms,
            String hemorrhages,
            String exudates,
            String cottonWoolSpots,
            String macularEdemaFinding,
            String venousBeading,
            String irma,
            String neovascularization,
            String vitreousHemorrhage,
            String retinalDetachment,
            String drGrade,
            String dmeGrade,
            boolean isAnnual,
            boolean isSixMonth,
            boolean isRefer,
            boolean isUrgent,
            boolean isLaser,
            boolean isVEGF,
            String notes
    ) throws Exception {

        Connection conn = null;
        try {
            conn = MySQLConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Database connection could not be established.");
            }
            conn.setAutoCommit(false);

            // 1. Create Image
            int imageId = -1;
            String imageSql = """
                INSERT INTO tblimage (image_name, image_type_id, image_data)
                VALUES (?, ?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(imageSql, Statement.RETURN_GENERATED_KEYS);
                 FileInputStream fis = new FileInputStream(reportImageFile)) {
                ps.setString(1, reportImageFile.getName());
                ps.setInt(2, 1); // Default type id = 1
                ps.setBinaryStream(3, fis, (int) reportImageFile.length());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) imageId = rs.getInt(1);
                }
            }
            if (imageId == -1) throw new SQLException("Failed to save report image.");

            // 2. Create Criticality
            int criticalityId = -1;
            String criticalitySql = """
                INSERT INTO tblcriticality (criticality_lvl, reasoning)
                VALUES (?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(criticalitySql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, criticality);
                ps.setString(2, doctorReasoning);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) criticalityId = rs.getInt(1);
                }
            }
            if (criticalityId == -1) throw new SQLException("Failed to save criticality.");

            // 3. Create Pathological Findings
            int findingsId = -1;
            String findingsSql = """
                INSERT INTO tblpathological
                (`1`, `2`, `3`, `4`, `5`, `6`, `7`, `8`, `9`, `10`)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(findingsSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, microaneurysms);
                ps.setString(2, hemorrhages);
                ps.setString(3, exudates);
                ps.setString(4, cottonWoolSpots);
                ps.setString(5, macularEdemaFinding);
                ps.setString(6, venousBeading);
                ps.setString(7, irma);
                ps.setString(8, neovascularization);
                ps.setString(9, vitreousHemorrhage);
                ps.setString(10, retinalDetachment);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) findingsId = rs.getInt(1);
                }
            }
            if (findingsId == -1) throw new SQLException("Failed to save pathological findings.");

            // 4. Create Evaluation
            int evaluationId = -1;
            String evaluationSql = """
                INSERT INTO tblfindings (final_dr_grade, macular_edema)
                VALUES (?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(evaluationSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, drGrade);
                ps.setString(2, dmeGrade);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) evaluationId = rs.getInt(1);
                }
            }
            if (evaluationId == -1) throw new SQLException("Failed to save evaluation.");

            // 5. Create Recommendations
            int recommendationsId = -1;
            String recSql = """
                INSERT INTO tblrecommendations
                (isAnnual, isSixMonth, isRefer, isUrgent, isLaser, isVEGF, final_notes)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(recSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setBoolean(1, isAnnual);
                ps.setBoolean(2, isSixMonth);
                ps.setBoolean(3, isRefer);
                ps.setBoolean(4, isUrgent);
                ps.setBoolean(5, isLaser);
                ps.setBoolean(6, isVEGF);
                ps.setString(7, notes);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) recommendationsId = rs.getInt(1);
                }
            }
            if (recommendationsId == -1) throw new SQLException("Failed to save recommendations.");

            // 6. Create Test
            int testId = -1;
            String testSql = """
                INSERT INTO tbltests (p_id, d_id, raw_img_id)
                VALUES (?, ?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(testSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, patientId);
                ps.setInt(2, doctorId);
                ps.setInt(3, imageId);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) testId = rs.getInt(1);
                }
            }
            if (testId == -1) throw new SQLException("Failed to save test record.");

            // 7. Create Report
            int reportId = -1;
            String reportSql = """
                INSERT INTO tblreport
                (test_id, criticality_id, findings_id, recommendations_id, evaluation_id)
                VALUES (?, ?, ?, ?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(reportSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, testId);
                ps.setInt(2, criticalityId);
                ps.setInt(3, findingsId);
                ps.setInt(4, recommendationsId);
                ps.setInt(5, evaluationId);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) reportId = rs.getInt(1);
                }
            }
            if (reportId == -1) throw new SQLException("Failed to save final report.");

            // Populate global AppContext with active transaction IDs
            org.example.daibetes.app.AppContext.getInstance().setCurrentTestId(testId);
            org.example.daibetes.app.AppContext.getInstance().setSelectedReportId(reportId);

            // Success: commit transaction
            conn.commit();
            return reportId;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
