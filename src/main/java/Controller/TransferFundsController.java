package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class TransferFundsController {

    @FXML private ComboBox<?> fromAccountComboBox;
    @FXML private ComboBox<?> toAccountComboBox;
    @FXML private TextField amountField;

    @FXML
    private void handleTransfer() {

        System.out.println("Transfer initiated");
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/dashboard-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) amountField.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}