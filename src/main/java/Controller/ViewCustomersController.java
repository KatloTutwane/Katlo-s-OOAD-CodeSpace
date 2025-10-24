package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ViewCustomersController {

    @FXML private TableView<?> customerTable;
    @FXML private TableColumn<?, ?> idColumn;
    @FXML private TableColumn<?, ?> firstNameColumn;
    @FXML private TableColumn<?, ?> lastNameColumn;
    @FXML private TableColumn<?, ?> emailColumn;
    @FXML private TableColumn<?, ?> usernameColumn;

    @FXML
    private void initialize() {
        // Setup table columns and load customer data
    }
}