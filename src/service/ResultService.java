package service;

import java.sql.SQLException;
import java.util.List;

import dao.ResultDAO;
import model.Result;

public class ResultService {
    private final ResultDAO resultDAO = new ResultDAO();
    public List<Result> getAllResults() throws SQLException { return resultDAO.getAllResults(); }
    public List<Result> getResultsByExamId(int examId) throws SQLException { return resultDAO.getResultsByExamId(examId); }
    public List<Result> getResultsByStudentId(int studentId) throws SQLException { return resultDAO.getResultsByStudentId(studentId); }
    // Used by Friend 1 student result history.
// Student history shows only the latest attempt per exam.
public List<Result> getHistory(int studentId) throws SQLException {
    return resultDAO.getLatestResultsByStudentId(studentId);
}    public String formatResult(Result result) { return String.format("%s%nScore: %d/%d%nPercentage: %.2f%%%nSubmitted: %s", result.getExamTitle(), result.getScore(), result.getTotalMarks(), result.getPercentage(), result.getSubmittedAt()); }
}
