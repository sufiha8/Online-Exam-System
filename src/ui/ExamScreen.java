
package ui;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.Exam;
import model.Question;
import model.Result;
import model.Student;
import service.ExamService;
import util.ExamTimer;

public class ExamScreen {

    private final Stage stage;
    private final Student student;
    private final Exam exam;
    private final List<Question> questions;
    private final ExamService service = new ExamService();

    private int index = 0;

    private final Label questionLabel = new Label();
    private final ToggleGroup options = new ToggleGroup();
    private final RadioButton[] radios = new RadioButton[4];
    private final Label timerLabel = new Label();

    private ExamTimer timer;
    private boolean submitted = false;

    public ExamScreen(
            Stage stage, Student student,
            Exam exam, List<Question> questions) {
        this.stage = stage;
        this.student = student;
        this.exam = exam;
        this.questions = questions;
    }

    public void show() {
        if (questions == null || questions.isEmpty()) {
            showMessage("No questions are available for this exam.");
            new StudentDashboard(stage, student).show();
            return;
        }

        Label heading = new Label(exam.getTitle());
        heading.getStyleClass().add("page-title");

        timerLabel.getStyleClass().add("timer-label");

        questionLabel.setWrapText(true);
        questionLabel.getStyleClass().add("question-text");

        VBox optionBox = new VBox(14);
        optionBox.getStyleClass().add("question-options");
        optionBox.setPadding(new Insets(18));

        for (int i = 0; i < 4; i++) {
            radios[i] = new RadioButton();
            radios[i].setToggleGroup(options);
            radios[i].setWrapText(true);
            radios[i].getStyleClass().add("answer-option");
            optionBox.getChildren().add(radios[i]);
        }

        Button previous = new Button("Previous");
        Button next = new Button("Next");
        Button submit = new Button("Submit Exam");

        previous.getStyleClass().add("secondary-button");
        next.getStyleClass().add("primary-button");
        submit.getStyleClass().add("danger-button");

        previous.setOnAction(e -> {
            saveAnswer();
            if (index > 0) {
                index--;
                render();
            }
        });

        next.setOnAction(e -> {
            saveAnswer();
            if (index < questions.size() - 1) {
                index++;
                render();
            }
        });

        submit.setOnAction(e -> {
            Alert confirm = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to submit your exam?",
                    ButtonType.YES, ButtonType.NO);

            confirm.setTitle("Submit Exam");
            confirm.setHeaderText("Confirm your submission");

            confirm.showAndWait().ifPresent(answer -> {
                if (answer == ButtonType.YES) {
                    submit(false);
                }
            });
        });

        HBox navigation = new HBox(
                12, previous, next, submit);
        navigation.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(
                18, heading, timerLabel, questionLabel,
                optionBox, navigation);

        root.setPadding(new Insets(28));
        VBox.setVgrow(optionBox, Priority.ALWAYS);

        render();

        timer = new ExamTimer(
                exam.getDurationMinutes(),
                seconds -> timerLabel.setText(
                        "Time remaining: "
                        + (seconds / 60) + ":"
                        + String.format("%02d", seconds % 60)),
                () -> submit(true));

        stage.setTitle("Online Exam - " + exam.getTitle());
        stage.setScene(UiStyle.createScene(root, 800, 600));
        stage.show();

        timer.start();
    }

    private void render() {
        Question question = questions.get(index);

        questionLabel.setText(
                "Question " + (index + 1)
                + " of " + questions.size()
                + "\n\n" + question.getQuestionText());

        String[] values = {
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD()
        };

        char[] labels = {'A', 'B', 'C', 'D'};

        options.selectToggle(null);

        for (int i = 0; i < 4; i++) {
            radios[i].setText(
                    labels[i] + ". " +
                    (values[i] == null ? "" : values[i]));

            radios[i].setUserData(labels[i]);

            radios[i].setSelected(
                    question.getSelectedAnswer() == labels[i]);
        }
    }

    private void saveAnswer() {
        Toggle selected = options.getSelectedToggle();

        questions.get(index).setSelectedAnswer(
                selected == null
                        ? '\0'
                        : (Character) selected.getUserData());
    }

    private void submit(boolean automatic) {
        if (submitted) {
            return;
        }

        saveAnswer();

        try {
            Result result = service.submitExam(
                    student, exam, questions);

            submitted = true;

            if (timer != null) {
                timer.stop();
            }

            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION);

            alert.setTitle("Exam Submitted");
            alert.setHeaderText(
                    automatic
                            ? "Time is up. Your exam was submitted automatically."
                            : "Your exam was submitted successfully.");

            alert.setContentText(String.format(
                    "Score: %d/%d%nPercentage: %.2f%%",
                    result.getScore(),
                    result.getTotalMarks(),
                    result.getPercentage()));

            alert.showAndWait();

            new StudentDashboard(stage, student).show();

        } catch (Exception ex) {
            Alert alert = new Alert(
                    Alert.AlertType.ERROR,
                    ex.getMessage() == null
                            ? "Unable to submit exam."
                            : ex.getMessage(),
                    ButtonType.OK);

            alert.setHeaderText("Could Not Submit Exam");
            alert.showAndWait();
        }
    }

    private void showMessage(String message) {
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
