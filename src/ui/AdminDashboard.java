package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Admin;

public class AdminDashboard {
    private final Stage stage; private final Admin admin;
    public AdminDashboard(Stage stage, Admin admin) { this.stage=stage; this.admin=admin; }
    public void show() {
        Label title=new Label("Admin Dashboard"); title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");
        Label welcome=new Label("Welcome, "+admin.getName()+" ("+admin.getEmail()+")");
        Button exams=new Button("Manage Exams"); Button questions=new Button("Manage Questions"); Button reports=new Button("Student Results & Reports"); Button logout=new Button("Logout");
        exams.setMaxWidth(Double.MAX_VALUE); questions.setMaxWidth(Double.MAX_VALUE); reports.setMaxWidth(Double.MAX_VALUE); logout.setMaxWidth(Double.MAX_VALUE);
        exams.setOnAction(e->new ExamManagement(stage,admin).show()); questions.setOnAction(e->new QuestionManagement(stage,admin).show()); reports.setOnAction(e->new ResultReports(stage,admin).show()); logout.setOnAction(e->new Login(stage).show());
        VBox root=new VBox(18,title,welcome,exams,questions,reports,logout); root.setAlignment(Pos.CENTER); root.setPadding(new Insets(35)); root.setMaxWidth(420);
        stage.setTitle("Online Exam System - Admin Dashboard"); stage.setScene(UiStyle.createScene(root,650,520)); stage.show();
    }
}
