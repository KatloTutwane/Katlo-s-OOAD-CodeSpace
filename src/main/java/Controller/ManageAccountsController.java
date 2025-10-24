package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import java.io.IOException;

public class ManageAccountsController {

    @FXML private TableView<?> accountTable;
    @FXML private TableColumn<?, ?> accIdColumn;
    @FXML private TableColumn<?, ?> custNameColumn;
    @FXML private TableColumn<?, ?> typeColumn;
    @FXML private TableColumn<?, ?> balanceColumn;

    @FXML
    private void handleCreateAccount() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/NewAccountPage.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) accountTable.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}