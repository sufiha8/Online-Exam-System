
package ui;

import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Admin;
import model.Exam;
import model.Question;
import service.AdminService;

public class QuestionManagement {

    private final Stage stage;
    private final Admin admin;
    private final AdminService service = new AdminService();

    private final ComboBox<Exam> examBox = new ComboBox<>();
    private final TableView<Question> table = new TableView<>();

    private final TextArea questionText = new TextArea();
    private final TextField a = new TextField();
    private final TextField b = new TextField();
    private final TextField c = new TextField();
    private final TextField d = new TextField();

    private final ComboBox<String> correct =
            new ComboBox<>(FXCollections.observableArrayList(
                    "A", "B", "C", "D"));

    public QuestionManagement(Stage stage, Admin admin) {
        this.stage = stage;
        this.admin = admin;
    }

    public void show() {
        Label heading = new Label("Question Management");
        heading.getStyleClass().add("page-title");

        Label subtitle = new Label(
                "Add questions and manage answer options for each exam.");
        subtitle.getStyleClass().add("muted-label");

        questionText.setPromptText("Enter the question");
        questionText.setPrefRowCount(2);
        questionText.setWrapText(true);

        a.setPromptText("Enter option A");
        b.setPromptText("Enter option B");
        c.setPromptText("Enter option C");
        d.setPromptText("Enter option D");

        correct.setValue("A");
        correct.setMaxWidth(Double.MAX_VALUE);
        examBox.setPromptText("Select an exam");
        examBox.setMaxWidth(Double.MAX_VALUE);

        examBox.setOnAction(e -> refresh());

        col("ID", "questionId", 55);
        col("Question", "questionText", 250);
        col("Option A", "optionA", 120);
        col("Option B", "optionB", 120);
        col("Option C", "optionC", 120);
        col("Option D", "optionD", 120);
        col("Correct", "correctAnswer", 75);

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(
                new Label("Select an exam to view its questions."));

        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldQuestion, question) -> {
                    if (question != null) {
                        questionText.setText(
                                question.getQuestionText());
                        a.setText(question.getOptionA());
                        b.setText(question.getOptionB());
                        c.setText(question.getOptionC());
                        d.setText(question.getOptionD());
                        correct.setValue(
                                String.valueOf(
                                        question.getCorrectAnswer()));
                    }
                });

        Button add = new Button("Add Question");
        Button update = new Button("Update Selected");
        Button delete = new Button("Delete Selected");
        Button clear = new Button("Clear Form");
        Button back = new Button("Back to Dashboard");

        add.getStyleClass().add("primary-button");
        update.getStyleClass().add("primary-button");
        delete.getStyleClass().add("danger-button");
        clear.getStyleClass().add("secondary-button");
        back.getStyleClass().add("secondary-button");

        Label status = new Label();
        status.setWrapText(true);

        add.setOnAction(e -> run(status, () -> {
            service.addQuestion(form(0));
            refresh();
            clearForm();
        }));

        update.setOnAction(e -> run(status, () -> {
            Question selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                throw new IllegalArgumentException(
                        "Select a question first.");
            }

            service.updateQuestion(
                    form(selected.getQuestionId()));

            refresh();
            status.setText("Question updated successfully.");
        }));

        delete.setOnAction(e -> {
            Question selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                status.setText("Select a question first.");
                return;
            }

            Alert confirm = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Delete the selected question?",
                    ButtonType.YES, ButtonType.NO);

            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText("Delete Question");

            confirm.showAndWait().ifPresent(answer -> {
                if (answer == ButtonType.YES) {
                    run(status, () -> {
                        service.deleteQuestion(
                                selected.getQuestionId());
                        refresh();
                        clearForm();
                        status.setText(
                                "Question deleted successfully.");
                    });
                }
            });
        });

        clear.setOnAction(e -> {
            clearForm();
            status.setText("");
        });

        back.setOnAction(
                e -> new AdminDashboard(stage, admin).show());

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(10);

        form.add(new Label("Exam"), 0, 0);
        form.add(examBox, 1, 0);

        form.add(new Label("Question"), 0, 1);
        form.add(questionText, 1, 1);

        form.add(new Label("Option A"), 0, 2);
        form.add(a, 1, 2);

        form.add(new Label("Option B"), 0, 3);
        form.add(b, 1, 3);

        form.add(new Label("Option C"), 0, 4);
        form.add(c, 1, 4);

        form.add(new Label("Option D"), 0, 5);
        form.add(d, 1, 5);

        form.add(new Label("Correct Answer"), 0, 6);
        form.add(correct, 1, 6);

        form.setMaxWidth(820);

        HBox actions = new HBox(
                10, add, update, delete, clear, back);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(
                14, heading, subtitle, form,
                actions, table, status);

        root.setPadding(new Insets(26));
        VBox.setVgrow(table, Priority.ALWAYS);

        loadExams();

        stage.setTitle(
                "Online Exam System - Question Management");
        stage.setScene(UiStyle.createScene(root, 1100, 760));
        stage.show();
    }

    private void col(String name, String property, int width) {
        TableColumn<Question, Object> column =
                new TableColumn<>(name);

        column.setCellValueFactory(
                new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    private Question form(int id) {
        Exam exam = examBox.getValue();

        if (exam == null) {
            throw new IllegalArgumentException(
                    "Create or select an exam first.");
        }

        if (questionText.getText().isBlank()
                || a.getText().isBlank()
                || b.getText().isBlank()
                || c.getText().isBlank()
                || d.getText().isBlank()) {
            throw new IllegalArgumentException(
                    "Fill in the question and all four options.");
        }

        Question question = new Question();
        question.setQuestionId(id);
        question.setExamId(exam.getExamId());
        question.setQuestionText(questionText.getText().trim());
        question.setOptionA(a.getText().trim());
        question.setOptionB(b.getText().trim());
        question.setOptionC(c.getText().trim());
        question.setOptionD(d.getText().trim());
        question.setCorrectAnswer(
                correct.getValue() == null
                        ? ' ' : correct.getValue().charAt(0));

        return question;
    }

    private void loadExams() {
        try {
            List<Exam> exams = service.getAllExams();
            examBox.setItems(
                    FXCollections.observableArrayList(exams));

            if (!exams.isEmpty()) {
                examBox.getSelectionModel().selectFirst();
            } else {
                new Alert(
                        Alert.AlertType.INFORMATION,
                        "Create an exam before adding questions.")
                        .showAndWait();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void refresh() {
        try {
            Exam exam = examBox.getValue();

            List<Question> questions = exam == null
                    ? Collections.emptyList()
                    : service.getQuestionsByExamId(
                            exam.getExamId());

            table.setItems(
                    FXCollections.observableArrayList(questions));

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void clearForm() {
        questionText.clear();
        a.clear();
        b.clear();
        c.clear();
        d.clear();
        correct.setValue("A");
        table.getSelectionModel().clearSelection();
    }

    private void run(Label status, Task task) {
        try {
            task.run();
            status.setText("Operation completed successfully.");
        } catch (Exception ex) {
            status.setText(
                    ex.getMessage() == null
                            ? "Operation failed."
                            : ex.getMessage());
        }
    }

    private void showError(Exception ex) {
        Alert alert = new Alert(
                Alert.AlertType.ERROR,
                ex.getMessage() == null
                        ? "An unexpected error occurred."
                        : ex.getMessage());
        alert.setHeaderText("Operation Failed");
        alert.showAndWait();
    }

    private interface Task {
        void run() throws Exception;
    }
}
