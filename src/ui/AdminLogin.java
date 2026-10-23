
package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Admin;
import service.AuthenticationService;

public class AdminLogin {
    private final Stage stage;
    private final AuthenticationService auth =
            new AuthenticationService();

    public AdminLogin(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Label title = new Label("Administrator Login");
        title.setStyle("-fx-font-size: 23px; -fx-font-weight: bold;");

        TextField email = new TextField();
        email.setPromptText("Admin email");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Label status = new Label();
        status.setWrapText(true);

        Button login = new Button("Login");
        login.getStyleClass().add("primary-button");
        Button register = new Button("Admin Registration");
        register.getStyleClass().add("primary-button");
        Button back = new Button("Back to Student Login");
        back.getStyleClass().add("secondary-button");
        title.getStyleClass().add("page-title");

        login.setOnAction(e -> {
            try {
                Admin admin = auth.loginAdmin(
                        email.getText(), password.getText());

                if (admin == null) {
                    status.setText(
                            "Invalid credentials or inactive admin.");
                } else {
                    new AdminDashboard(stage, admin).show();
                }
            } catch (Exception ex) {
                status.setText("Login failed: " + ex.getMessage());
            }
        });

        register.setOnAction(e ->
                new AdminRegistration(stage).show());

        back.setOnAction(e -> new Login(stage).show());

        VBox root = new VBox(
                14, title, email, password,
                login, register, back, status);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setMaxWidth(360);

        stage.setTitle("Online Exam System - Admin Login");
stage.setScene(UiStyle.createScene(root, 500, 420));        stage.show();
    }
}
