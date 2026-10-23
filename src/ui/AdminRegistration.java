
package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.AuthenticationService;

public class AdminRegistration {

    private final Stage stage;
    private final AuthenticationService auth =
            new AuthenticationService();

    public AdminRegistration(Stage stage) {
        this.stage = stage;
    }

    public void show() {

        Label title = new Label("Admin Registration");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label(
                "Create an administrator account");
        subtitle.getStyleClass().add("muted-label");

        TextField name = new TextField();
        name.setPromptText("Admin full name");
        name.setMaxWidth(Double.MAX_VALUE);

        TextField email = new TextField();
        email.setPromptText("Email (@onlineexam.com)");
        email.setMaxWidth(Double.MAX_VALUE);

        PasswordField password = new PasswordField();
        password.setPromptText("Create password");
        password.setMaxWidth(Double.MAX_VALUE);

        PasswordField confirm = new PasswordField();
        confirm.setPromptText("Confirm password");
        confirm.setMaxWidth(Double.MAX_VALUE);

        Label status = new Label();
        status.setWrapText(true);
        status.setMaxWidth(Double.MAX_VALUE);

        Button register = new Button("Register Admin");
        register.getStyleClass().add("primary-button");
        register.setMaxWidth(Double.MAX_VALUE);

        Button back = new Button("Back to Admin Login");
        back.setMaxWidth(Double.MAX_VALUE);

        register.setOnAction(e -> {

            String adminName = name.getText().trim();
            String adminEmail =
                    email.getText().trim().toLowerCase();
            String adminPassword = password.getText();

            if (adminName.isBlank()
                    || adminEmail.isBlank()
                    || adminPassword.isBlank()
                    || confirm.getText().isBlank()) {

                status.setText("Please fill in all fields.");
                status.setStyle("-fx-text-fill: #b91c1c;");
                return;
            }

            if (!adminEmail.matches(
                    "^[^\\s@]+@onlineexam\\.com$")) {

                status.setText(
                        "Use an email ending with @onlineexam.com.");
                status.setStyle("-fx-text-fill: #b91c1c;");
                return;
            }

            if (adminPassword.length() < 8) {
                status.setText(
                        "Password must contain at least 8 characters.");
                status.setStyle("-fx-text-fill: #b91c1c;");
                return;
            }

            if (!adminPassword.equals(confirm.getText())) {
                status.setText("Passwords do not match.");
                status.setStyle("-fx-text-fill: #b91c1c;");
                return;
            }

            try {
                boolean success = auth.registerAdmin(
                        adminName, adminEmail, adminPassword);

                if (success) {
                    status.setText(
                            "Registration successful! "
                          + "Your account requires activation "
                          + "before you can log in.");
                    status.setStyle("-fx-text-fill: #15803d;");

                    name.clear();
                    email.clear();
                    password.clear();
                    confirm.clear();

                } else {
                    status.setText(
                            "Registration was unsuccessful.");
                    status.setStyle("-fx-text-fill: #b91c1c;");
                }

            } catch (Exception ex) {
                status.setText(
                        "Registration failed: " + ex.getMessage());
                status.setStyle("-fx-text-fill: #b91c1c;");
            }
        });

        back.setOnAction(e ->
                new AdminLogin(stage).show());

        VBox root = new VBox(
                16,
                title,
                subtitle,
                name,
                email,
                password,
                confirm,
                register,
                back,
                status
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(32));
        root.setMaxWidth(420);

        // Apply the shared stylesheet
        Scene scene = UiStyle.createScene(root, 540, 560);

        stage.setTitle("Online Exam System - Admin Registration");
        stage.setScene(scene);
        stage.show();
    }
}
