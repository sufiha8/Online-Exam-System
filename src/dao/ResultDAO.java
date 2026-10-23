
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Result;
import util.DatabaseConnection;

public class ResultDAO {

    public List<Result> getAllResults() throws SQLException {
        return query(null, null);
    }

    public List<Result> getResultsByExamId(int examId) throws SQLException {
        return query("r.exam_id", examId);
    }

    public List<Result> getResultsByStudentId(int studentId) throws SQLException {
        return query("r.student_id", studentId);
    }

    private List<Result> query(String filter, Integer value)
            throws SQLException {

        List<Result> list = new ArrayList<>();

        String sql =
            "SELECT r.result_id, r.student_id, s.name AS student_name, " +
            "s.email AS student_email, r.exam_id, e.title, " +
            "r.score, r.total_marks, r.percentage, r.submitted_at " +
            "FROM results r " +
            "JOIN exams e ON e.exam_id = r.exam_id " +
            "JOIN students s ON s.student_id = r.student_id";

        if (filter != null) {
            sql += " WHERE " + filter + " = ?";
        }

        sql += " ORDER BY r.submitted_at DESC";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            if (value != null) {
                p.setInt(1, value);
            }

            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    Timestamp ts = r.getTimestamp("submitted_at");

                    Result result = new Result(
                        r.getInt("result_id"),
                        r.getInt("student_id"),
                        r.getInt("exam_id"),
                        r.getString("title"),
                        r.getString("student_name"),
                        r.getString("student_email"),
                        r.getInt("score"),
                        r.getInt("total_marks"),
                        r.getDouble("percentage"),
                        ts == null ? null : ts.toLocalDateTime()
                    );

                    list.add(result);
                }
            }
        }

        return list;
    }
    
    // Student history: return only the latest attempt for each exam.
    public List<Result> getLatestResultsByStudentId(int studentId)
            throws SQLException {

        List<Result> list = new ArrayList<>();

        String sql =
            "SELECT r.result_id, r.student_id, s.name AS student_name, " +
            "s.email AS student_email, r.exam_id, e.title, " +
            "r.score, r.total_marks, r.percentage, r.submitted_at " +
            "FROM results r " +
            "JOIN exams e ON e.exam_id = r.exam_id " +
            "JOIN students s ON s.student_id = r.student_id " +
            "WHERE r.student_id = ? " +
            "AND r.result_id = (" +
            "  SELECT r2.result_id " +
            "  FROM results r2 " +
            "  WHERE r2.student_id = r.student_id " +
            "  AND r2.exam_id = r.exam_id " +
            "  ORDER BY r2.submitted_at DESC, r2.result_id DESC " +
            "  LIMIT 1" +
            ") " +
            "ORDER BY r.submitted_at DESC";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, studentId);

            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    Timestamp ts = r.getTimestamp("submitted_at");

                    Result result = new Result(
                        r.getInt("result_id"),
                        r.getInt("student_id"),
                        r.getInt("exam_id"),
                        r.getString("title"),
                        r.getString("student_name"),
                        r.getString("student_email"),
                        r.getInt("score"),
                        r.getInt("total_marks"),
                        r.getDouble("percentage"),
                        ts == null ? null : ts.toLocalDateTime()
                    );

                    list.add(result);
                }
            }
        }

        return list;
    }

}
