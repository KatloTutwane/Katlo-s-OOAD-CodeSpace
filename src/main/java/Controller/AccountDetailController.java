package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class AccountDetailController {

    @FXML private Label accountIdLabel;
    @FXML private Label accountTypeLabel;
    @FXML private Label balanceLabel;
    @FXML private TextField depositField;
    @FXML private TextField withdrawField;

    @FXML
    private void handleDeposit() {
        // Implement deposit logic
        String amount = depositField.getText();
        System.out.println("Depositing: " + amount);
    }

    @FXML
    private void handleWithdraw() {
        // Implement withdraw logic
        String amount = withdrawField.getText();
        System.out.println("Withdrawing: " + amount);
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/AccountListPage.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) accountIdLabel.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}