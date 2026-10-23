package dao;

import model.Question;
import util.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class QuestionDAO {
    public List<Question> getQuestionsByExamId(int examId) throws SQLException {
        List<Question> list=new ArrayList<>();
        String sql="SELECT question_id,exam_id,question_text,option_a,option_b,option_c,option_d,correct_answer FROM questions WHERE exam_id=? ORDER BY question_id";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setInt(1,examId); try(ResultSet r=p.executeQuery()) { while(r.next()) list.add(new Question(r.getInt("question_id"),r.getInt("exam_id"),r.getString("question_text"),r.getString("option_a"),r.getString("option_b"),r.getString("option_c"),r.getString("option_d"),r.getString("correct_answer").charAt(0))); }
        }
        return list;
    }
    public boolean addQuestion(Question q) throws SQLException {
        String sql="INSERT INTO questions(exam_id,question_text,option_a,option_b,option_c,option_d,correct_answer) VALUES(?,?,?,?,?,?,?)";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)) { bind(p,q); return p.executeUpdate()==1; }
    }
    public boolean updateQuestion(Question q) throws SQLException {
        String sql="UPDATE questions SET exam_id=?,question_text=?,option_a=?,option_b=?,option_c=?,option_d=?,correct_answer=? WHERE question_id=?";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)) { bind(p,q); p.setInt(8,q.getQuestionId()); return p.executeUpdate()==1; }
    }
    private void bind(PreparedStatement p, Question q) throws SQLException {
        p.setInt(1,q.getExamId()); p.setString(2,q.getQuestionText().trim()); p.setString(3,q.getOptionA().trim()); p.setString(4,q.getOptionB().trim()); p.setString(5,q.getOptionC().trim()); p.setString(6,q.getOptionD().trim()); p.setString(7,String.valueOf(Character.toUpperCase(q.getCorrectAnswer())));
    }
    public boolean deleteQuestion(int id) throws SQLException {
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement p=c.prepareStatement("DELETE FROM questions WHERE question_id=?")) { p.setInt(1,id); return p.executeUpdate()==1; }
    }
}
