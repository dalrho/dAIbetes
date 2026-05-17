package org.example.daibetes.core.database;

import javafx.scene.image.Image;
import org.example.daibetes.modules.doctor.ui.review.model.ReportData ;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReportDataDAO {

    public ReportRefs getReportRefsByReportId(int reportId) {
        String sql = """
        SELECT
            r.test_id,
            r.criticality_id,
            r.findings_id,
            r.evaluation_id,
            r.recommendations_id,
            t.raw_img_id
        FROM tblreport r
        INNER JOIN tbltests t
            ON r.test_id = t.test_id
        WHERE r.report_id = ?
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reportId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ReportRefs(
                        rs.getInt("test_id"),
                        rs.getInt("criticality_id"),
                        rs.getInt("findings_id"),
                        rs.getInt("evaluation_id"),
                        rs.getInt("recommendations_id"),
                        rs.getInt("raw_img_id")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateImage(int imageId, byte[] imageData) {
        String sql = """
        UPDATE tblimage
        SET image_data = ?
        WHERE img_id = ?
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBytes(1, imageData);
            ps.setInt(2, imageId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCriticality(int criticalityId, String criticalityLevel, String reasoning) {
        String sql = """
        UPDATE tblcriticality
        SET criticality_lvl = ?,
            reasoning = ?
        WHERE criticality_id = ?
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, criticalityLevel);
            ps.setString(2, reasoning);
            ps.setInt(3, criticalityId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePathologicalFindings(
            int findingsId,
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
        UPDATE tblpathological
        SET `1` = ?,
            `2` = ?,
            `3` = ?,
            `4` = ?,
            `5` = ?,
            `6` = ?,
            `7` = ?,
            `8` = ?,
            `9` = ?,
            `10` = ?
        WHERE findings_id = ?
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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
            ps.setInt(11, findingsId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEvaluation(int evaluationId, String finalDrGrade, String macularEdema) {
        String sql = """
        UPDATE tblfindings
        SET final_dr_grade = ?,
            macular_edema = ?
        WHERE evaluation_id = ?
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, finalDrGrade);
            ps.setString(2, macularEdema);
            ps.setInt(3, evaluationId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRecommendations(
            int recommendationId,
            boolean isAnnual,
            boolean isSixMonth,
            boolean isRefer,
            boolean isUrgent,
            boolean isLaser,
            boolean isVEGF,
            String finalNotes
    ) {
        String sql = """
        UPDATE tblrecommendations
        SET isAnnual = ?,
            isSixMonth = ?,
            isRefer = ?,
            isUrgent = ?,
            isLaser = ?,
            isVEGF = ?,
            final_notes = ?
        WHERE recommendation_id = ?
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, isAnnual ? 1 : 0);
            ps.setInt(2, isSixMonth ? 1 : 0);
            ps.setInt(3, isRefer ? 1 : 0);
            ps.setInt(4, isUrgent ? 1 : 0);
            ps.setInt(5, isLaser ? 1 : 0);
            ps.setInt(6, isVEGF ? 1 : 0);
            ps.setString(7, finalNotes);
            ps.setInt(8, recommendationId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateReportSavedOn(int reportId) {
        String sql = """
        UPDATE tblreport
        SET saved_on = CURRENT_TIMESTAMP
        WHERE report_id = ?
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reportId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ReportData getReportDataByReportId(int reportId) {
        String sql = """
        SELECT
            img.image_data,

            c.criticality_lvl,
            c.reasoning,

            p.`1` AS microaneurysms,
            p.`2` AS hemorrhages,
            p.`3` AS hard_exudates,
            p.`4` AS cotton_wool_spots,
            p.`5` AS macular_edema,
            p.`6` AS venous_beading,
            p.`7` AS irma,
            p.`8` AS neovascularization,
            p.`9` AS vitreous_hemorrhage,
            p.`10` AS retinal_detachment,

            e.final_dr_grade,
            e.macular_edema AS dme_grade,

            rec.isAnnual,
            rec.isSixMonth,
            rec.isRefer,
            rec.isUrgent,
            rec.isLaser,
            rec.isVEGF,
            rec.final_notes

        FROM tblreport r

        INNER JOIN tbltests t
            ON r.test_id = t.test_id

        INNER JOIN tblimage img
            ON t.raw_img_id = img.img_id

        INNER JOIN tblcriticality c
            ON r.criticality_id = c.criticality_id

        INNER JOIN tblpathological p
            ON r.findings_id = p.findings_id

        INNER JOIN tblfindings e
            ON r.evaluation_id = e.evaluation_id

        INNER JOIN tblrecommendations rec
            ON r.recommendations_id = rec.recommendation_id

        WHERE r.report_id = ?
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reportId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ReportData data = new ReportData();

                byte[] imageBytes = rs.getBytes("image_data");

                if (imageBytes != null) {
                    data.setScanImage(new Image(new ByteArrayInputStream(imageBytes)));
                }

                data.setCriticality(rs.getString("criticality_lvl"));
                data.setCriticalityReasoning(rs.getString("reasoning"));

                data.setMicroaneurysms(rs.getString("microaneurysms"));
                data.setHemorrhages(rs.getString("hemorrhages"));
                data.setHardExudates(rs.getString("hard_exudates"));
                data.setCottonWoolSpots(rs.getString("cotton_wool_spots"));
                data.setMacularEdema(rs.getString("macular_edema"));
                data.setVenousBeading(rs.getString("venous_beading"));

                data.setIrma(rs.getString("irma"));
                data.setNeovascularization(rs.getString("neovascularization"));
                data.setVitreousHemorrhage(rs.getString("vitreous_hemorrhage"));
                data.setRetinalDetachment(rs.getString("retinal_detachment"));

                data.setDrGrade(rs.getString("final_dr_grade"));
                data.setDmeGrade(rs.getString("dme_grade"));

                List<String> recommendations = new ArrayList<>();

                if (rs.getInt("isAnnual") == 1) {
                    recommendations.add("Annual Follow-up");
                }

                if (rs.getInt("isSixMonth") == 1) {
                    recommendations.add("6-month Follow-up");
                }

                if (rs.getInt("isRefer") == 1) {
                    recommendations.add("Refer to Specialist");
                }

                if (rs.getInt("isUrgent") == 1) {
                    recommendations.add("Urgent Evaluation");
                }

                if (rs.getInt("isLaser") == 1) {
                    recommendations.add("Laser Treatment");
                }

                if (rs.getInt("isVEGF") == 1) {
                    recommendations.add("Anti-VEGF Therapy");
                }

                data.setRecommendations(recommendations);
                data.setClinicalNotes(rs.getString("final_notes"));

                return data;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public static class ReportRefs {
        private final int testId;
        private final int criticalityId;
        private final int findingsId;
        private final int evaluationId;
        private final int recommendationsId;
        private final int rawImageId;

        public ReportRefs(
                int testId,
                int criticalityId,
                int findingsId,
                int evaluationId,
                int recommendationsId,
                int rawImageId
        ) {
            this.testId = testId;
            this.criticalityId = criticalityId;
            this.findingsId = findingsId;
            this.evaluationId = evaluationId;
            this.recommendationsId = recommendationsId;
            this.rawImageId = rawImageId;
        }

        public int getTestId() {
            return testId;
        }

        public int getCriticalityId() {
            return criticalityId;
        }

        public int getFindingsId() {
            return findingsId;
        }

        public int getEvaluationId() {
            return evaluationId;
        }

        public int getRecommendationsId() {
            return recommendationsId;
        }

        public int getRawImageId() {
            return rawImageId;
        }
    }
}