package org.example.daibetes.core.database;

import org.example.daibetes.modules.doctor.ui.patients.MyPatientReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.example.daibetes.modules.doctor.ui.patients.MyPatientCard;
import org.example.daibetes.modules.doctor.ui.patients.MyPatientReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.example.daibetes.modules.records.model.Record;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MyPatientsDAO {

    public List<MyPatientReport> getReportsByDoctorId(int doctorId) {
        List<MyPatientReport> reports = new ArrayList<>();

        String sql = """
            SELECT
                r.report_id,
                r.test_id,
                t.p_id,
                CONCAT(u.firstname, ' ', u.lastname) AS patient_name,
                r.saved_on,
                c.criticality_lvl
            FROM tblreport r
            INNER JOIN tbltests t
                ON r.test_id = t.test_id
            INNER JOIN tblpatient p
                ON t.p_id = p.p_id
            INNER JOIN tbluser u
                ON p.user_id = u.user_id
            INNER JOIN tblcriticality c
                ON r.criticality_id = c.criticality_id
            WHERE t.d_id = ?
            ORDER BY r.saved_on DESC
        """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Get reports failed: database connection is null.");
                return reports;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, doctorId);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    reports.add(new MyPatientReport(
                            rs.getInt("report_id"),
                            rs.getInt("test_id"),
                            rs.getInt("p_id"),
                            rs.getString("patient_name"),
                            rs.getTimestamp("saved_on").toLocalDateTime(),
                            rs.getString("criticality_lvl")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reports;
    }

    public List<MyPatientCard> getPatientCardsByDoctorId(int doctorId) {
        List<MyPatientCard> cards = new ArrayList<>();

        String sql = """
        SELECT
            r.report_id,
            r.test_id,
            t.p_id,
            CONCAT(u.firstname, ' ', u.lastname) AS patient_name,
            r.saved_on,
            c.criticality_lvl
        FROM tblreport r
        INNER JOIN tbltests t
            ON r.test_id = t.test_id
        INNER JOIN tblpatient p
            ON t.p_id = p.p_id
        INNER JOIN tbluser u
            ON p.user_id = u.user_id
        INNER JOIN tblcriticality c
            ON r.criticality_id = c.criticality_id
        WHERE t.d_id = ?
        ORDER BY t.p_id ASC, r.saved_on DESC
    """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Get patient cards failed: database connection is null.");
                return cards;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, doctorId);

                ResultSet rs = ps.executeQuery();

                Map<Integer, List<MyPatientReport>> groupedReports = new LinkedHashMap<>();

                while (rs.next()) {
                    MyPatientReport report = new MyPatientReport(
                            rs.getInt("report_id"),
                            rs.getInt("test_id"),
                            rs.getInt("p_id"),
                            rs.getString("patient_name"),
                            rs.getTimestamp("saved_on").toLocalDateTime(),
                            rs.getString("criticality_lvl")
                    );

                    groupedReports
                            .computeIfAbsent(report.getPatientId(), id -> new ArrayList<>())
                            .add(report);
                }

                for (List<MyPatientReport> reports : groupedReports.values()) {
                    if (reports.isEmpty()) continue;

                    MyPatientReport latestReport = reports.get(0);

                    cards.add(new MyPatientCard(
                            latestReport.getPatientId(),
                            latestReport.getPatientName(),
                            latestReport.getLastReported(),
                            latestReport.getCriticalityLevel(),
                            reports
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cards;
    }

    public List<MyPatientReport> getReportsByPatientAndDoctor(int patientId, int doctorId) {
        List<MyPatientReport> reports = new ArrayList<>();

        String sql = """
        SELECT
            r.report_id,
            r.test_id,
            t.p_id,
            CONCAT(u.firstname, ' ', u.lastname) AS patient_name,
            r.saved_on,
            c.criticality_lvl
        FROM tblreport r
        INNER JOIN tbltests t
            ON r.test_id = t.test_id
        INNER JOIN tblpatient p
            ON t.p_id = p.p_id
        INNER JOIN tbluser u
            ON p.user_id = u.user_id
        INNER JOIN tblcriticality c
            ON r.criticality_id = c.criticality_id
        WHERE t.p_id = ?
          AND t.d_id = ?
        ORDER BY r.saved_on DESC
    """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Database connection is null.");
                return reports;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, patientId);
                ps.setInt(2, doctorId);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    reports.add(new MyPatientReport(
                            rs.getInt("report_id"),
                            rs.getInt("test_id"),
                            rs.getInt("p_id"),
                            rs.getString("patient_name"),
                            rs.getTimestamp("saved_on").toLocalDateTime(),
                            rs.getString("criticality_lvl")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reports;
    }
    public List<Record> getRecordTableByPatientAndDoctor(int patientId, int doctorId) {
        List<Record> records = new ArrayList<>();

        String sql = """
        SELECT
            r.report_id,
            t.p_id,
            CONCAT(u.firstname, ' ', u.lastname) AS patient_name,
            DATE_FORMAT(r.saved_on, '%Y-%m-%d %h:%i %p') AS scan_date,
            CASE
                WHEN rec.isAnnual = 1 OR rec.isSixMonth = 1 OR rec.isUrgent = 1
                THEN 'YES'
                ELSE 'NO'
            END AS follow_up,
            c.criticality_lvl
        FROM tblreport r
        INNER JOIN tbltests t
            ON r.test_id = t.test_id
        INNER JOIN tblpatient p
            ON t.p_id = p.p_id
        INNER JOIN tbluser u
            ON p.user_id = u.user_id
        INNER JOIN tblcriticality c
            ON r.criticality_id = c.criticality_id
        INNER JOIN tblrecommendations rec
            ON r.recommendations_id = rec.recommendation_id
        WHERE t.p_id = ?
          AND t.d_id = ?
        ORDER BY r.saved_on DESC
    """;

        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Database connection is null.");
                return records;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, patientId);
                ps.setInt(2, doctorId);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    records.add(new Record(
                            rs.getInt("report_id"),
                            String.valueOf(rs.getInt("p_id")),
                            rs.getString("patient_name"),
                            rs.getString("scan_date"),
                            rs.getString("follow_up"),
                            rs.getString("criticality_lvl")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return records;
    }
}