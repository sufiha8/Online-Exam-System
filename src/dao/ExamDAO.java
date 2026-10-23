package dao;

import model.Exam;
import util.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class ExamDAO {
    private static final String SELECT = "SELECT e.exam_id,e.title,e.description,e.duration_minutes, " +
        "(SELECT COUNT(*) FROM questions q WHERE q.exam_id=e.exam_id) AS question_count,e.is_available FROM exams e ";

    public List<Exam> getAllExams() throws SQLException { return getExams(false); }
    public List<Exam> getAvailableExams() throws SQLException { return getExams(true); }
    private List<Exam> getExams(boolean availableOnly) throws SQLException {
        List<Exam> list = new ArrayList<>();
        String sql = SELECT + (availableOnly ? "WHERE e.is_available=1 " : "") + "ORDER BY e.exam_id DESC";
        try (Connection c=DatabaseConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql); ResultSet r=p.executeQuery()) {
            while(r.next()) list.add(new Exam(r.getInt("exam_id"),r.getString("title"),r.getString("description"),r.getInt("duration_minutes"),r.getInt("question_count"),r.getBoolean("is_available")));
        }
        return list;
    }
    public boolean addExam(Exam e) throws SQLException {
        String sql="INSERT INTO exams(title,description,duration_minutes,is_available) VALUES(?,?,?,?)";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,e.getTitle().trim()); p.setString(2,e.getDescription()); p.setInt(3,e.getDurationMinutes()); p.setBoolean(4,e.isAvailable()); return p.executeUpdate()==1;
        }
    }
    public boolean updateExam(Exam e) throws SQLException {
        String sql="UPDATE exams SET title=?,description=?,duration_minutes=?,is_available=? WHERE exam_id=?";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,e.getTitle().trim()); p.setString(2,e.getDescription()); p.setInt(3,e.getDurationMinutes()); p.setBoolean(4,e.isAvailable()); p.setInt(5,e.getExamId()); return p.executeUpdate()==1;
        }
    }
    public boolean deleteExam(int id) throws SQLException {
        String sql="DELETE FROM exams WHERE exam_id=?";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)) { p.setInt(1,id); return p.executeUpdate()==1; }
    }
}
