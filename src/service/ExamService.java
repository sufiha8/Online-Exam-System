
package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Exam;
import model.Question;
import model.Result;
import model.Student;
import util.DatabaseConnection;
import util.PasswordUtil;

public class ExamService {

    public boolean registerStudent(
            String name, String email, String password, String phone)
            throws Exception {

        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.length() < 8
                || phone == null || phone.isBlank()) {
            throw new IllegalArgumentException(
                    "Fill in all fields. Password must be at least 8 characters.");
        }

        String cleanEmail = email.trim().toLowerCase();

        if (!cleanEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }

        String sql =
                "INSERT INTO students(name,email,password,phone) VALUES(?,?,?,?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, name.trim());
            p.setString(2, cleanEmail);
            p.setString(3, PasswordUtil.hash(password));
            p.setString(4, phone.trim());

            return p.executeUpdate() == 1;
        }
    }

    public Student login(String email, String password) throws Exception {
        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            return null;
        }

        String sql =
                "SELECT student_id,name,email,password,phone "
              + "FROM students WHERE email=?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, email.trim().toLowerCase());

            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) {
                    return null;
                }

                String storedPassword = r.getString("password");

                if (!PasswordUtil.verify(password, storedPassword)) {
                    return null;
                }

                if (!storedPassword.startsWith("PBKDF2:")) {
                    storedPassword = PasswordUtil.hash(password);

                    try (PreparedStatement update = c.prepareStatement(
                            "UPDATE students SET password=? WHERE student_id=?")) {
                        update.setString(1, storedPassword);
                        update.setInt(2, r.getInt("student_id"));
                        update.executeUpdate();
                    }
                }

                return new Student(
                        r.getInt("student_id"),
                        r.getString("name"),
                        r.getString("email"),
                        storedPassword,
                        r.getString("phone"));
            }
        }
    }

    public List<Exam> getAvailableExams() throws SQLException {
        List<Exam> exams = new ArrayList<>();

        String sql =
                "SELECT e.exam_id,e.title,e.description,e.duration_minutes,"
              + "(SELECT COUNT(*) FROM questions q WHERE q.exam_id=e.exam_id) "
              + "AS question_count,e.is_available "
              + "FROM exams e WHERE e.is_available=1 "
              + "ORDER BY e.exam_id DESC";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {

            while (r.next()) {
                exams.add(new Exam(
                        r.getInt("exam_id"),
                        r.getString("title"),
                        r.getString("description"),
                        r.getInt("duration_minutes"),
                        r.getInt("question_count"),
                        r.getBoolean("is_available")));
            }
        }

        return exams;
    }

    public List<Question> getRandomQuestions(
            int examId, int requestedCount) throws SQLException {

        List<Question> questions = new ArrayList<>();

        String sql =
                "SELECT question_id,exam_id,question_text,"
              + "option_a,option_b,option_c,option_d,correct_answer "
              + "FROM questions WHERE exam_id=? ORDER BY RAND() LIMIT ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, examId);
            p.setInt(2, Math.max(1, requestedCount));

            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    String answer = r.getString("correct_answer");

                    questions.add(new Question(
                            r.getInt("question_id"),
                            r.getInt("exam_id"),
                            r.getString("question_text"),
                            r.getString("option_a"),
                            r.getString("option_b"),
                            r.getString("option_c"),
                            r.getString("option_d"),
                            answer.charAt(0)));
                }
            }
        }

        return questions;
    }

    public Result submitExam(
            Student student, Exam exam, List<Question> questions)
            throws SQLException {

        int score = 0;

        for (Question q : questions) {
            if (q.getSelectedAnswer() != '\0'
                    && Character.toUpperCase(q.getSelectedAnswer())
                    == Character.toUpperCase(q.getCorrectAnswer())) {
                score++;
            }
        }

        int total = questions.size();
        double percentage = total == 0 ? 0 : score * 100.0 / total;

        String sql =
                "INSERT INTO results(student_id,exam_id,score,total_marks,"
              + "percentage,submitted_at) VALUES(?,?,?,?,?,NOW())";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            p.setInt(1, student.getStudentId());
            p.setInt(2, exam.getExamId());
            p.setInt(3, score);
            p.setInt(4, total);
            p.setDouble(5, percentage);
            p.executeUpdate();

            try (ResultSet keys = p.getGeneratedKeys()) {
                int id = keys.next() ? keys.getInt(1) : 0;

                return new Result(
                        id, student.getStudentId(), exam.getExamId(),
                        exam.getTitle(), score, total, percentage,
                        java.time.LocalDateTime.now());
            }
        }
    }

    public List<Result> getResultHistory(int studentId) throws SQLException {
        List<Result> results = new ArrayList<>();

        String sql =
                "SELECT r.result_id, r.student_id, r.exam_id, e.title, "
              + "r.score, r.total_marks, r.percentage, r.submitted_at "
              + "FROM results r "
              + "JOIN exams e ON e.exam_id = r.exam_id "
              + "WHERE r.student_id = ? "
              + "AND r.result_id = ( "
              + "SELECT r2.result_id FROM results r2 "
              + "WHERE r2.student_id = r.student_id "
              + "AND r2.exam_id = r.exam_id "
              + "ORDER BY r2.submitted_at DESC, r2.result_id DESC LIMIT 1 "
              + ") ORDER BY r.submitted_at DESC";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, studentId);

            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    Timestamp timestamp = r.getTimestamp("submitted_at");

                    results.add(new Result(
                            r.getInt("result_id"),
                            r.getInt("student_id"),
                            r.getInt("exam_id"),
                            r.getString("title"),
                            r.getInt("score"),
                            r.getInt("total_marks"),
                            r.getDouble("percentage"),
                            timestamp == null ? null
                                    : timestamp.toLocalDateTime()));
                }
            }
        }

        return results;
    }
}
