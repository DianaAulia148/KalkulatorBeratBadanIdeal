// MainApp.java
package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale locale = new Locale("id"); // Default Indonesian
        ResourceBundle bundle = ResourceBundle.getBundle("localization.bundle", locale);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"), bundle);
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle(bundle.getString("app.title"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
