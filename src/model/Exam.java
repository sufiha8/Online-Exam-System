package model;

public class Exam {
    private int examId;
    private String title;
    private String description;
    private int durationMinutes;
    private int totalQuestions;
    private boolean available;

    public Exam() {}
    public Exam(int examId, String title, String description, int durationMinutes, int totalQuestions, boolean available) {
        this.examId = examId; this.title = title; this.description = description;
        this.durationMinutes = durationMinutes; this.totalQuestions = totalQuestions; this.available = available;
    }
    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    @Override public String toString() { return title + " (" + durationMinutes + " min)"; }
}
