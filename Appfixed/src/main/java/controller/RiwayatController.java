
package controller;

import dao.UserDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.stage.Stage;
import util.Session;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class RiwayatController {

    @FXML private Label usernameLabel;
    @FXML private ListView<String> historyList;
    @FXML private Button backButton;

    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        String username = Session.getCurrentUsername();
        usernameLabel.setText("Riwayat " + username);

        List<String> history = UserDAO.getUserHistory(username);
        historyList.setItems(FXCollections.observableArrayList(history));

        // Set resource bundle agar bisa digunakan di backToCalculator
        Locale locale = new Locale("id", "ID");
        this.bundle = ResourceBundle.getBundle("localization.Bundle", locale);
    }

    @FXML
    private void backToCalculator() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Kalkulator.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            // Ambil controller lebih awal untuk setUsername
            KalkulatorController controller = loader.getController();
            controller.setUsername(Session.getCurrentUsername());

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Set fullscreen setelah setScene
            stage.setTitle(bundle.getString("ideal.weight.calculator"));
            stage.setFullScreen(true);


 // atau setFullScreen(true)
            stage.show();

        } catch (IOException e) {
            showAlert("Gagal kembali ke kalkulator:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
