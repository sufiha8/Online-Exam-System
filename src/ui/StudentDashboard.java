
package ui;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Exam;
import model.Result;
import model.Student;
import service.ExamService;

public class StudentDashboard {

    private final Stage stage;
    private final Student student;
    private final ExamService service = new ExamService();
    private final ListView<Exam> examList = new ListView<>();

    public StudentDashboard(Stage stage, Student student) {
        this.stage = stage;
        this.student = student;
    }

    public void show() {

        // Welcome heading
        Label heading = new Label(
                "Welcome, " + student.getName());
        heading.getStyleClass().add("page-title");

        Label subtitle = new Label(
                "Select an available exam to begin.");
        subtitle.getStyleClass().add("muted-label");

        Label examHeading = new Label("Available Exams");
        examHeading.getStyleClass().add("section-title");

        // Buttons
        Button start = new Button("Start Selected Exam");
        Button history = new Button("Result History");
        Button logout = new Button("Logout");

        start.getStyleClass().add("primary-button");
        logout.getStyleClass().add("danger-button");

        start.setMaxWidth(Double.MAX_VALUE);
        history.setMaxWidth(Double.MAX_VALUE);
        logout.setMaxWidth(Double.MAX_VALUE);

        // Status message
        Label status = new Label();
        status.setWrapText(true);

        // Start selected exam
        start.setOnAction(e -> {
            Exam exam =
                    examList.getSelectionModel().getSelectedItem();

            if (exam == null) {
                status.setText("Please select an exam first.");
                return;
            }

            try {
                List<model.Question> questions =
                        service.getRandomQuestions(
                                exam.getExamId(),
                                exam.getTotalQuestions());

                if (questions.isEmpty()) {
                    status.setText(
                            "This exam does not have any questions yet.");
                    return;
                }

                new ExamScreen(
                        stage, student, exam, questions).show();

            } catch (Exception ex) {
                showError(ex);
            }
        });

        // Result history
        history.setOnAction(e -> showHistory());

        // Logout
        logout.setOnAction(e -> new Login(stage).show());

        HBox buttons = new HBox(12, start, history, logout);
        buttons.setAlignment(Pos.CENTER_LEFT);

        // Main dashboard layout
        VBox root = new VBox(
                16,
                heading,
                subtitle,
                examHeading,
                examList,
                buttons,
                status
        );

        root.setPadding(new Insets(28));
        root.setFillWidth(true);

        VBox.setVgrow(examList, Priority.ALWAYS);

        // Load available exams
        try {
            examList.setItems(
                    FXCollections.observableArrayList(
                            service.getAvailableExams()));

            if (examList.getItems().isEmpty()) {
                status.setText(
                        "No exams are currently available.");
            }

        } catch (Exception ex) {
            showError(ex);
        }

        // Apply shared CSS theme
        stage.setTitle("Online Exam System - Student Dashboard");

        Scene scene = UiStyle.createScene(root, 800, 580);
        stage.setScene(scene);
        stage.show();
    }

    // Display result history
    private void showHistory() {
        try {
            List<Result> results =
                    service.getResultHistory(
                            student.getStudentId());

            if (results.isEmpty()) {
                alert(
                        "Result History",
                        "You have not submitted any exams yet.");
                return;
            }

            StringBuilder message = new StringBuilder();

            for (Result r : results) {
                message.append("Subject: ")
                        .append(r.getExamTitle())
                        .append("\nScore: ")
                        .append(r.getScore())
                        .append("/")
                        .append(r.getTotalMarks())
                        .append("\nPercentage: ")
                        .append(String.format(
                                "%.2f%%", r.getPercentage()))
                        .append("\nSubmitted: ")
                        .append(r.getSubmittedAt())
                        .append("\n")
                        .append("------------------------------")
                        .append("\n\n");
            }

            TextArea historyText =
                    new TextArea(message.toString());

            historyText.setEditable(false);
            historyText.setWrapText(true);
            historyText.setPrefColumnCount(55);
            historyText.setPrefRowCount(
                    Math.max(8, results.size() * 5));

            Alert historyAlert =
                    new Alert(Alert.AlertType.INFORMATION);

            historyAlert.setTitle("Result History");
            historyAlert.setHeaderText(
                    "Your Exam Results (" + results.size() + " exams)");

            historyAlert.getDialogPane().setContent(historyText);
            historyAlert.getDialogPane().setPrefWidth(580);
            historyAlert.getDialogPane().setPrefHeight(420);

            // Apply CSS to the result history dialog
            var css = UiStyle.class.getResource("/css/app.css");

            if (css != null) {
                historyAlert.getDialogPane()
                        .getStylesheets()
                        .add(css.toExternalForm());
            }

            historyAlert.showAndWait();

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void alert(String title, String message) {
        Alert a = new Alert(
                Alert.AlertType.INFORMATION,
                message,
                ButtonType.OK);

        a.setTitle(title);
        a.setHeaderText(null);

        var css = UiStyle.class.getResource("/css/app.css");

        if (css != null) {
            a.getDialogPane()
                    .getStylesheets()
                    .add(css.toExternalForm());
        }

        a.showAndWait();
    }

    private void showError(Exception ex) {
        Alert a = new Alert(
                Alert.AlertType.ERROR,
                ex.getMessage() == null
                        ? "An unexpected error occurred."
                        : ex.getMessage(),
                ButtonType.OK);

        a.setTitle("Error");
        a.setHeaderText("Operation failed");

        var css = UiStyle.class.getResource("/css/app.css");

        if (css != null) {
            a.getDialogPane()
                    .getStylesheets()
                    .add(css.toExternalForm());
        }

        a.showAndWait();
    }
}
