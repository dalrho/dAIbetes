package org.example.daibetes.core.database;

import java.sql.*;

public class ReportDAO {

    public int createCriticality(String criticalityLevel, String reasoning) {
        String sql = """
            INSERT INTO tblcriticality (criticality_lvl, reasoning)
            VALUES (?, ?)
        """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) return -1;

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, criticalityLevel);
                ps.setString(2, reasoning);

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int createPathologicalFindings(
            String microaneurysms,
            String hemorrhages,
            String exudates,
            String cottonWoolSpots,
            String macularEdema,
            String venousBeading,
            String irma,
            String neovascularization,
            String vitreousHemorrhage,
            String retinalDetachment
    ) {
        String sql = """
            INSERT INTO tblpathological
            (`1`, `2`, `3`, `4`, `5`, `6`, `7`, `8`, `9`, `10`)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) return -1;

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, microaneurysms);
                ps.setString(2, hemorrhages);
                ps.setString(3, exudates);
                ps.setString(4, cottonWoolSpots);
                ps.setString(5, macularEdema);
                ps.setString(6, venousBeading);
                ps.setString(7, irma);
                ps.setString(8, neovascularization);
                ps.setString(9, vitreousHemorrhage);
                ps.setString(10, retinalDetachment);

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int createEvaluation(String finalDrGrade, String macularEdema) {
        String sql = """
            INSERT INTO tblfindings (final_dr_grade, macular_edema)
            VALUES (?, ?)
        """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) return -1;

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, finalDrGrade);
                ps.setString(2, macularEdema);

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int createRecommendations(
            boolean isAnnual,
            boolean isSixMonth,
            boolean isRefer,
            boolean isUrgent,
            boolean isLaser,
            boolean isVEGF,
            String finalNotes
    ) {
        String sql = """
            INSERT INTO tblrecommendations
            (isAnnual, isSixMonth, isRefer, isUrgent, isLaser, isVEGF, final_notes)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) return -1;

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setBoolean(1, isAnnual);
                ps.setBoolean(2, isSixMonth);
                ps.setBoolean(3, isRefer);
                ps.setBoolean(4, isUrgent);
                ps.setBoolean(5, isLaser);
                ps.setBoolean(6, isVEGF);
                ps.setString(7, finalNotes);

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int createReport(
            int testId,
            int criticalityId,
            int findingsId,
            int recommendationsId,
            int evaluationId
    ) {
        String sql = """
            INSERT INTO tblreport
            (test_id, criticality_id, findings_id, recommendations_id, evaluation_id)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) return -1;

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, testId);
                ps.setInt(2, criticalityId);
                ps.setInt(3, findingsId);
                ps.setInt(4, recommendationsId);
                ps.setInt(5, evaluationId);

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}