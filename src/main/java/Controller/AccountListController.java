package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import java.io.IOException;

public class AccountListController {

    @FXML private TableView<?> accountTable;
    @FXML private TableColumn<?, ?> accountIdColumn;
    @FXML private TableColumn<?, ?> accountTypeColumn;
    @FXML private TableColumn<?, ?> balanceColumn;

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/dashboard-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) accountTable.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}