
package model;

import java.time.LocalDateTime;

public class Result {
    private int resultId;
    private int studentId;
    private int examId;
    private String examTitle;
    private String studentName;
    private String studentEmail;
    private int score;
    private int totalMarks;
    private double percentage;
    private LocalDateTime submittedAt;

    public Result() {}

    // Original constructor: keeps compatibility with existing code
    public Result(int resultId, int studentId, int examId, String examTitle,
                  int score, int totalMarks, double percentage,
                  LocalDateTime submittedAt) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.examId = examId;
        this.examTitle = examTitle;
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = percentage;
        this.submittedAt = submittedAt;
    }

    // Constructor including student details
    public Result(int resultId, int studentId, int examId, String examTitle,
                  String studentName, String studentEmail, int score,
                  int totalMarks, double percentage,
                  LocalDateTime submittedAt) {
        this(resultId, studentId, examId, examTitle, score,
             totalMarks, percentage, submittedAt);
        this.studentName = studentName;
        this.studentEmail = studentEmail;
    }

    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
