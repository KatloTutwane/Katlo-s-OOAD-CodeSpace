package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import java.io.IOException;

public class TransactionHistoryController {

    @FXML private TableView<?> transactionTable;
    @FXML private TableColumn<?, ?> dateColumn;
    @FXML private TableColumn<?, ?> typeColumn;
    @FXML private TableColumn<?, ?> amountColumn;
    @FXML private TableColumn<?, ?> balanceColumn;

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/AccountListPage.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) transactionTable.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}