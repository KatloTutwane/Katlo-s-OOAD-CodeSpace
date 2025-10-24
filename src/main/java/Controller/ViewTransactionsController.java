package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ViewTransactionsController {

    @FXML private TableView<?> transactionTable;
    @FXML private TableColumn<?, ?> transIdColumn;
    @FXML private TableColumn<?, ?> custNameColumn;
    @FXML private TableColumn<?, ?> accIdColumn;
    @FXML private TableColumn<?, ?> typeColumn;
    @FXML private TableColumn<?, ?> amountColumn;
    @FXML private TableColumn<?, ?> dateColumn;

    // Initialize method to set up table data
    @FXML
    private void initialize() {
        // Setup table columns and data
    }
}