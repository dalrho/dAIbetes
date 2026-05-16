package org.example.daibetes.core.database;

import org.example.daibetes.modules.doctor.ui.patients.MyPatientReport;

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
}