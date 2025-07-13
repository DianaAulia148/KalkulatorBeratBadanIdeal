package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import util.Session;
import util.BMICalculatorUtil;
import dao.UserDAO;
import view.GaugeView;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.concurrent.Task;

public class KalkulatorController {

    @FXML private Label titleLabel, userLabel;
    @FXML private Button logoutButton, historyButton, calculateButton;
    @FXML private ComboBox<String> languageComboBox;

    @FXML private TextField heightField, weightField, resultField, ageField;
    @FXML private TextArea adviceArea;

    @FXML private Label resultLabel, adviceLabel, categoryLabel;
    @FXML private Label ageLabel, heightLabel, weightLabel;
    @FXML private Label unitCmLabel, unitKgLabel, categoryLabelLabel;

    @FXML private Pane gaugeContainer;
    private GaugeView gaugeView;

    private ResourceBundle bundle;
    private String currentUsername;
    private Locale currentLocale;

    @FXML
    private void initialize() {
        // Pilihan bahasa
        languageComboBox.getItems().addAll("Indonesia", "English");
        languageComboBox.setValue("Indonesia");
        currentLocale = new Locale("id", "ID");
        setBundle(currentLocale);

        languageComboBox.setOnAction(e -> {
            String selected = languageComboBox.getValue();
            currentLocale = selected.equals("English") ? new Locale("en") : new Locale("id", "ID");
            setBundle(currentLocale);
        });

        currentUsername = Session.getCurrentUsername();
        if (currentUsername != null && bundle != null) {
            userLabel.setText(bundle.getString("welcome") + ", " + currentUsername);
        }

        gaugeView = new GaugeView();
        gaugeContainer.getChildren().add(gaugeView);
    }

    public void setUsername(String username) {
        currentUsername = username;
        Session.setCurrentUsername(username);
        if (bundle != null) {
            userLabel.setText(bundle.getString("welcome") + ", " + username);
        }
    }

    private void setBundle(Locale locale) {
        try {
            bundle = ResourceBundle.getBundle("localization.Bundle", locale);
            updateTexts();
        } catch (Exception e) {
            System.err.println("Gagal load resource bundle: " + e.getMessage());
        }
    }

    private void updateTexts() {
        if (bundle != null) {
            titleLabel.setText(bundle.getString("ideal.weight.calculator"));
            logoutButton.setText(bundle.getString("logout"));
            historyButton.setText(bundle.getString("history"));
            calculateButton.setText(bundle.getString("calculate"));

            ageLabel.setText(bundle.getString("age"));
            heightLabel.setText(bundle.getString("height"));
            weightLabel.setText(bundle.getString("weight"));

            ageField.setPromptText(bundle.getString("age.placeholder"));
            heightField.setPromptText(bundle.getString("height.placeholder"));
            weightField.setPromptText(bundle.getString("weight.placeholder"));

            resultLabel.setText(bundle.getString("result"));
            adviceLabel.setText(bundle.getString("advice"));
            categoryLabelLabel.setText(bundle.getString("category.label"));

            unitCmLabel.setText("cm");
            unitKgLabel.setText("kg");

            if (currentUsername != null) {
                userLabel.setText(bundle.getString("welcome") + ", " + currentUsername);
            }
        }
    }

    @FXML
public void calculateBMI() {
    Task<Void> task = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            try {
                double height = Double.parseDouble(heightField.getText());
                double weight = Double.parseDouble(weightField.getText());

                double bmi = BMICalculatorUtil.calculateBMI(weight, height);
                String status = BMICalculatorUtil.getStatus(bmi);
                String advice = BMICalculatorUtil.getAdvice(bmi);
                String labelKategori = BMICalculatorUtil.getCategoryLabel(bmi);

                List<String> history = UserDAO.getHistory(currentUsername);
                double previousWeight = -1;
                if (!history.isEmpty()) {
                    String lastEntry = history.get(history.size() - 1);
                    try {
                        int beratIndex = lastEntry.indexOf("Berat: ");
                        int kgIndex = lastEntry.indexOf(" kg", beratIndex);
                        if (beratIndex != -1 && kgIndex != -1) {
                            String beratStr = lastEntry.substring(beratIndex + 7, kgIndex).trim();
                            previousWeight = Double.parseDouble(beratStr);
                        }
                    } catch (Exception ex) {
                        System.err.println("Gagal membaca berat sebelumnya: " + ex.getMessage());
                    }
                }

                String perubahan = "";
                if (previousWeight > 0) {
                    if (weight > previousWeight) {
                        perubahan = "Naik";
                    } else if (weight < previousWeight) {
                        perubahan = "Turun";
                    } else {
                        perubahan = "Stabil";
                    }
                }

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String record = String.format(
                        "%s | Berat: %.1f kg | Tinggi: %.1f cm | BMI: %.2f | Status: %s%s",
                        timestamp, weight, height, bmi, status,
                        (perubahan.isEmpty() ? "" : " | Berat badan: " + perubahan)
                );

                UserDAO.saveHistory(currentUsername, record);

                // Update UI safely on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    resultField.setText(String.format("%.2f", bmi));
                    adviceArea.setText(advice);
                    categoryLabel.setText(labelKategori);
                    gaugeView.animateToBMI(bmi);
                });

            } catch (NumberFormatException e) {
                javafx.application.Platform.runLater(() -> {
                    showAlert(bundle.getString("error.invalidInput"));
                });
            }
            return null;
        }
    };

    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
}

    @FXML
    public void logout() {
        Session.clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(bundle.getString("login.title"));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Gagal logout.\n" + e.getMessage());
        }
    }

@FXML
public void goToRiwayat() {
    try {
        System.out.println("Navigasi ke halaman Riwayat...");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Riwayat.fxml"));
        loader.setResources(bundle); // untuk bahasa
        Parent root = loader.load();

        System.out.println("FXML Riwayat berhasil dimuat");

        Stage stage = (Stage) historyButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setWidth(850);
        stage.setHeight(600);
        stage.centerOnScreen();
        stage.setTitle(bundle.getString("history"));
        stage.show();

    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Gagal membuka halaman riwayat.\n" + e.getMessage());
    }
}

   
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString("info.title"));
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
