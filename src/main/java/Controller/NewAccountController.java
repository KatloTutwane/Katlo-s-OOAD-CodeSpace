package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class NewAccountController {

    @FXML private ComboBox<String> accountTypeComboBox;
    @FXML private TextField initialDepositField;

    @FXML
    private void initialize() {
        // Populate account type combo box
        accountTypeComboBox.getItems().addAll("Savings", "Checking", "Business");
    }

    @FXML
    private void handleCreateAccount() {
        // Implement account creation logic here
        String accountType = accountTypeComboBox.getValue();
        String initialDeposit = initialDepositField.getText();
        System.out.println("Creating " + accountType + " account with $" + initialDeposit);
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/dashboard-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) initialDepositField.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}