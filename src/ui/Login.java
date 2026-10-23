
package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Student;
import service.ExamService;

public class Login {
    private final Stage stage;
    private final ExamService service = new ExamService();

    public Login(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Label title = new Label("Online Exam System");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField email = new TextField();
        email.setPromptText("Student email");

        PasswordField password = new PasswordField();
        password.setPromptText("Student password");

        TextField name = new TextField();
        name.setPromptText("Full name");

        TextField regEmail = new TextField();
        regEmail.setPromptText("Email address");

        PasswordField regPassword = new PasswordField();
        regPassword.setPromptText("Create password");

        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm password");

        TextField phone = new TextField();
        phone.setPromptText("Phone number");

        Label status = new Label();
        status.setWrapText(true);

        Button login = new Button("Student Login");
        Button register = new Button("Register Student");
        Button admin = new Button("Admin Login");
        Button adminRegister = new Button("Admin Registration");
        title.getStyleClass().add("page-title");
login.getStyleClass().add("primary-button");
register.getStyleClass().add("primary-button");
adminRegister.getStyleClass().add("primary-button");

        login.setOnAction(e -> {
            try {
                Student student =
                        service.login(email.getText().trim(),
                                      password.getText());

                if (student == null) {
                    status.setText("Invalid email or password.");
                } else {
                    new StudentDashboard(stage, student).show();
                }
            } catch (Exception ex) {
                status.setText("Login failed: " + ex.getMessage());
            }
        });

        register.setOnAction(e -> {
            String studentName = name.getText().trim();
            String studentEmail = regEmail.getText().trim();
            String studentPassword = regPassword.getText();
            String confirm = confirmPassword.getText();
            String studentPhone = phone.getText().trim();

            if (studentName.isBlank() || studentEmail.isBlank()
                    || studentPassword.isBlank() || confirm.isBlank()
                    || studentPhone.isBlank()) {
                status.setText("Please fill in every registration field.");
                return;
            }

            if (!studentEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                status.setText("Enter a valid email address.");
                return;
            }

            if (studentPassword.length() < 8) {
                status.setText("Password must contain at least 8 characters.");
                return;
            }

            if (!studentPassword.equals(confirm)) {
                status.setText("Passwords do not match.");
                return;
            }

            try {
                boolean success = service.registerStudent(
                        studentName, studentEmail,
                        studentPassword, studentPhone);

                if (success) {
                    status.setText("Registration successful. Please log in.");
                    name.clear();
                    regEmail.clear();
                    regPassword.clear();
                    confirmPassword.clear();
                    phone.clear();
                } else {
                    status.setText("Registration failed. Check whether the email already exists.");
                }
            } catch (Exception ex) {
                status.setText("Registration failed: " + ex.getMessage());
            }
        });

        admin.setOnAction(e -> new AdminLogin(stage).show());

        adminRegister.setOnAction(e ->
                new AdminRegistration(stage).show());

        VBox root = new VBox(
                10,
                title,
                new Label("Student Login"),
                email,
                password,
                login,
                new Separator(),
                new Label("New Student Registration"),
                name,
                regEmail,
                regPassword,
                confirmPassword,
                phone,
                register,
                new Separator(),
                new HBox(10, admin, adminRegister),
                status
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));
        root.setMaxWidth(390);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        stage.setTitle("Online Exam System");
stage.setScene(UiStyle.createScene(scroll, 540, 700));        stage.show();
    }
}
