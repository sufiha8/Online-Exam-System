import javafx.application.Application;
import javafx.stage.Stage;
import ui.Login;

public class Main extends Application {
    @Override public void start(Stage stage) {
        new Login(stage).show();
    }
    public static void main(String[] args) { launch(args); }
}
