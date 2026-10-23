package service;

import dao.ExamDAO;
import dao.QuestionDAO;
import model.Exam;
import model.Question;
import java.sql.SQLException;
import java.util.List;

public class AdminService {
    private final ExamDAO examDAO=new ExamDAO();
    private final QuestionDAO questionDAO=new QuestionDAO();
    public List<Exam> getAllExams() throws SQLException { return examDAO.getAllExams(); }
    public boolean createExam(Exam e) throws SQLException { validateExam(e); return examDAO.addExam(e); }
    public boolean updateExam(Exam e) throws SQLException { validateExam(e); if(e.getExamId()<=0) throw new IllegalArgumentException("Select an exam first."); return examDAO.updateExam(e); }
    public boolean deleteExam(int id) throws SQLException { if(id<=0) throw new IllegalArgumentException("Select an exam first."); return examDAO.deleteExam(id); }
    public List<Question> getQuestionsByExamId(int id) throws SQLException { return questionDAO.getQuestionsByExamId(id); }
    public boolean addQuestion(Question q) throws SQLException { validateQuestion(q); return questionDAO.addQuestion(q); }
    public boolean updateQuestion(Question q) throws SQLException { validateQuestion(q); if(q.getQuestionId()<=0) throw new IllegalArgumentException("Select a question first."); return questionDAO.updateQuestion(q); }
    public boolean deleteQuestion(int id) throws SQLException { if(id<=0) throw new IllegalArgumentException("Select a question first."); return questionDAO.deleteQuestion(id); }
    private void validateExam(Exam e) { if(e.getTitle()==null||e.getTitle().isBlank()) throw new IllegalArgumentException("Exam title is required."); if(e.getDurationMinutes()<1) throw new IllegalArgumentException("Duration must be at least 1 minute."); }
    private void validateQuestion(Question q) { if(q.getExamId()<=0) throw new IllegalArgumentException("Select an exam."); if(blank(q.getQuestionText())||blank(q.getOptionA())||blank(q.getOptionB())||blank(q.getOptionC())||blank(q.getOptionD())) throw new IllegalArgumentException("Enter the question and all four options."); char a=Character.toUpperCase(q.getCorrectAnswer()); if("ABCD".indexOf(a)<0) throw new IllegalArgumentException("Choose correct answer A, B, C, or D."); }
    private boolean blank(String s) { return s==null||s.isBlank(); }
}
