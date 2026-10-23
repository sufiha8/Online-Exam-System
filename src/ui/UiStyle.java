
package ui;

import javafx.scene.Parent;
import javafx.scene.Scene;

public final class UiStyle {

    private UiStyle() {
    }

    public static Scene createScene(Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);

        var css = UiStyle.class.getResource("/css/app.css");

        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.err.println(
                    "CSS not found: place app.css in "
                    + "src/main/resources/css/app.css");
        }

        return scene;
    }
}
