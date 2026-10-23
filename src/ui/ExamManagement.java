
package ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
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
import service.AdminService;

public class ExamManagement {

    private final Stage stage;
    private final Admin admin;
    private final AdminService service = new AdminService();

    private final TableView<Exam> table = new TableView<>();
    private final TextField title = new TextField();
    private final TextArea description = new TextArea();
    private final Spinner<Integer> duration =
            new Spinner<>(1, 600, 30);
    private final CheckBox available =
            new CheckBox("Published / visible to students");

    public ExamManagement(Stage stage, Admin admin) {
        this.stage = stage;
        this.admin = admin;
    }

    public void show() {
        Label heading = new Label("Exam Management");
        heading.getStyleClass().add("page-title");

        Label subtitle = new Label(
                "Create, edit and manage available examinations.");
        subtitle.getStyleClass().add("muted-label");

        title.setPromptText("Enter exam title");
        description.setPromptText("Enter exam description");
        description.setPrefRowCount(3);
        description.setWrapText(true);
        duration.setEditable(true);
        available.setSelected(true);

        addCol("ID", "examId", 70);
        addCol("Title", "title", 180);
        addCol("Duration (min)", "durationMinutes", 110);
        addCol("Questions", "totalQuestions", 90);
        addCol("Available", "available", 90);

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(
                new Label("No exams found. Create your first exam."));

        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldExam, exam) -> {
                    if (exam != null) {
                        title.setText(exam.getTitle());
                        description.setText(
                                exam.getDescription() == null
                                        ? "" : exam.getDescription());
                        duration.getValueFactory().setValue(
                                exam.getDurationMinutes());
                        available.setSelected(exam.isAvailable());
                    }
                });

        Button add = new Button("Create Exam");
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
            service.createExam(formExam(0));
            refresh();
            clearForm();
        }));

        update.setOnAction(e -> run(status, () -> {
            Exam selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                throw new IllegalArgumentException(
                        "Select an exam from the table first.");
            }

            service.updateExam(formExam(selected.getExamId()));
            refresh();
            status.setText("Exam updated successfully.");
        }));

        delete.setOnAction(e -> {
            Exam selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                status.setText("Select an exam first.");
                return;
            }

            Alert confirm = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Delete this exam and its related questions/results?",
                    ButtonType.YES, ButtonType.NO);

            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText("Delete Exam");

            confirm.showAndWait().ifPresent(answer -> {
                if (answer == ButtonType.YES) {
                    run(status, () -> {
                        service.deleteExam(selected.getExamId());
                        refresh();
                        clearForm();
                        status.setText("Exam deleted successfully.");
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
        form.setVgap(12);
        form.add(new Label("Exam Title"), 0, 0);
        form.add(title, 1, 0);
        form.add(new Label("Duration (minutes)"), 0, 1);
        form.add(duration, 1, 1);
        form.add(new Label("Description"), 0, 2);
        form.add(description, 1, 2);
        form.add(available, 1, 3);
        form.setMaxWidth(760);

        HBox actions = new HBox(
                10, add, update, delete, clear, back);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(6, 0, 6, 0));

        VBox root = new VBox(
                14, heading, subtitle, form, actions, table, status);
        root.setPadding(new Insets(26));
        VBox.setVgrow(table, Priority.ALWAYS);

        refresh();

        stage.setTitle("Online Exam System - Exam Management");
        stage.setScene(UiStyle.createScene(root, 1050, 700));
        stage.show();
    }

    private void addCol(
            String name, String property, int width) {
        TableColumn<Exam, Object> column =
                new TableColumn<>(name);
        column.setCellValueFactory(
                new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    private Exam formExam(int id) {
        if (title.getText().isBlank()) {
            throw new IllegalArgumentException(
                    "Enter an exam title.");
        }

        Exam exam = new Exam();
        exam.setExamId(id);
        exam.setTitle(title.getText().trim());
        exam.setDescription(description.getText().trim());
        exam.setDurationMinutes(duration.getValue());
        exam.setAvailable(available.isSelected());
        return exam;
    }

    private void refresh() {
        try {
            table.setItems(FXCollections.observableArrayList(
                    service.getAllExams()));
        } catch (Exception ex) {
            error(ex);
        }
    }

    private void clearForm() {
        title.clear();
        description.clear();
        duration.getValueFactory().setValue(30);
        available.setSelected(true);
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

    private void error(Exception ex) {
        Alert alert = new Alert(
                Alert.AlertType.ERROR,
                "Could not load exams: " + ex.getMessage());
        alert.setHeaderText("Database Error");
        alert.showAndWait();
    }

    private interface Task {
        void run() throws Exception;
    }
}
