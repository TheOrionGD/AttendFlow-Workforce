package com.ams.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ams.model.AttendanceRecord;
import com.ams.util.DBUtil;

public class AttendanceDAO {

    public void saveAttendance(String date, List<AttendanceRecord> records) throws SQLException {
        String deleteSql = "DELETE FROM attendance WHERE date = ?";
        String insertSql = "INSERT INTO attendance (student_id, date, status) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setDate(1, Date.valueOf(date));
                deleteStmt.executeUpdate();
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (AttendanceRecord record : records) {
                    insertStmt.setInt(1, record.getStudentId());
                    insertStmt.setDate(2, Date.valueOf(date));
                    insertStmt.setString(3, record.getStatus());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
            conn.commit();
        }
    }

    public List<AttendanceRecord> getAttendanceByDate(String date) throws SQLException {
        String baseSql = "SELECT a.id, a.student_id, s.name as student_name, DATE_FORMAT(a.date, '%Y-%m-%d') AS date, a.status " +
                "FROM attendance a JOIN students s ON a.student_id = s.id";
        if (date != null && !date.trim().isEmpty()) {
            baseSql += " WHERE a.date = ?";
        }
        baseSql += " ORDER BY a.date DESC, s.name";

        List<AttendanceRecord> records = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(baseSql)) {
            if (date != null && !date.trim().isEmpty()) {
                stmt.setDate(1, Date.valueOf(date));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecord record = new AttendanceRecord();
                    record.setId(rs.getInt("id"));
                    record.setStudentId(rs.getInt("student_id"));
                    record.setStudentName(rs.getString("student_name"));
                    record.setDate(rs.getString("date"));
                    record.setStatus(rs.getString("status"));
                    records.add(record);
                }
            }
        }
        return records;
    }
}
