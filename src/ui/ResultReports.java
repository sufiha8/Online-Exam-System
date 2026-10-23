
package ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Admin;
import model.Result;
import service.ResultService;

public class ResultReports {

    private final Stage stage;
    private final Admin admin;
    private final ResultService service = new ResultService();

    private final TableView<Result> table = new TableView<>();
    private final Label statusLabel = new Label();

    public ResultReports(Stage stage, Admin admin) {
        this.stage = stage;
        this.admin = admin;
    }

    public void show() {
        Label heading = new Label("Student Results & Reports");
        heading.getStyleClass().add("page-title");

        Label subtitle = new Label(
                "View student details, exam scores and performance.");
        subtitle.getStyleClass().add("muted-label");

        col("Result ID", "resultId", 75);
        col("Student ID", "studentId", 85);
        col("Student Name", "studentName", 150);
        col("Student Email", "studentEmail", 190);
        col("Exam ID", "examId", 75);
        col("Exam / Subject", "examTitle", 170);
        col("Score", "score", 70);
        col("Total Marks", "totalMarks", 85);
        col("Percentage", "percentage", 95);
        col("Submitted At", "submittedAt", 160);

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        table.setPlaceholder(
                new Label("No results found. Complete an exam first."));

        TextField studentId = new TextField();
        studentId.setPromptText("Student ID");
        studentId.setPrefWidth(100);

        TextField examId = new TextField();
        examId.setPromptText("Exam ID");
        examId.setPrefWidth(100);

        Button all = new Button("All Results");
        Button byStudent = new Button("Search Student");
        Button byExam = new Button("Search Exam");
        Button back = new Button("Back to Dashboard");

        all.getStyleClass().add("primary-button");
        byStudent.getStyleClass().add("primary-button");
        byExam.getStyleClass().add("primary-button");
        back.getStyleClass().add("secondary-button");

        all.setOnAction(e -> load(0, 0));

        byStudent.setOnAction(e -> {
            try {
                int id = Integer.parseInt(
                        studentId.getText().trim());

                if (id <= 0) {
                    statusLabel.setText(
                            "Enter a valid positive student ID.");
                    return;
                }

                load(1, id);

            } catch (NumberFormatException ex) {
                statusLabel.setText(
                        "Enter a valid student ID.");
            }
        });

        byExam.setOnAction(e -> {
            try {
                int id = Integer.parseInt(
                        examId.getText().trim());

                if (id <= 0) {
                    statusLabel.setText(
                            "Enter a valid positive exam ID.");
                    return;
                }

                load(2, id);

            } catch (NumberFormatException ex) {
                statusLabel.setText("Enter a valid exam ID.");
            }
        });

        back.setOnAction(
                e -> new AdminDashboard(stage, admin).show());

        HBox toolbar = new HBox(
                10, all, studentId, byStudent,
                examId, byExam, back);

        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(8, 0, 8, 0));

        statusLabel.setWrapText(true);
        statusLabel.getStyleClass().add("muted-label");

        VBox root = new VBox(
                14, heading, subtitle, toolbar,
                table, statusLabel);

        root.setPadding(new Insets(26));
        VBox.setVgrow(table, Priority.ALWAYS);

        load(0, 0);

        stage.setTitle(
                "Online Exam System - Student Results & Reports");
        stage.setScene(UiStyle.createScene(root, 1250, 700));
        stage.show();
    }

    private void col(
            String name, String property, int width) {

        TableColumn<Result, Object> column =
                new TableColumn<>(name);

        column.setCellValueFactory(
                new PropertyValueFactory<>(property));

        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    private void load(int mode, int id) {
        try {
            if (mode == 1) {
                table.setItems(
                        FXCollections.observableArrayList(
                                service.getResultsByStudentId(id)));

            } else if (mode == 2) {
                table.setItems(
                        FXCollections.observableArrayList(
                                service.getResultsByExamId(id)));

            } else {
                table.setItems(
                        FXCollections.observableArrayList(
                                service.getAllResults()));
            }

            statusLabel.setText(
                    table.getItems().size()
                    + " result(s) found.");

        } catch (Exception ex) {
            statusLabel.setText(
                    "Could not load results: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
