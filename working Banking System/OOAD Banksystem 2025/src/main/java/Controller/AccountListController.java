package Controller;

import Model.Account;
import Model.Customer;
import dao.AccountDAO;
import dao.AccountDAOImpl;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.concurrent.Task;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class AccountListController {

    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account, String> accountIdColumn;
    @FXML private TableColumn<Account, String> accountTypeColumn;
    @FXML private TableColumn<Account, Double> balanceColumn;

    @FXML private Label customerNameLabel;

    private Customer customer;
    private AccountDAO accountDAO;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        initializeDAO();
        initializeTable();
        updateCustomerInfo();
        loadCustomerAccounts();
    }

    private void initializeDAO() {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521/XEPDB1", "bank_user", "bank123"
            );
            this.accountDAO = new AccountDAOImpl(conn);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Cannot connect to database: " + e.getMessage());
        }
    }

    private void initializeTable() {
        // Initialize table columns
        accountIdColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));


        // Format balance to show currency
        balanceColumn.setCellFactory(tc -> new javafx.scene.control.TableCell<Account, Double>() {
            @Override
            protected void updateItem(Double balance, boolean empty) {
                super.updateItem(balance, empty);
                if (empty || balance == null) {
                    setText(null);
                } else {
                    setText(String.format("P%.2f", balance));
                }
            }
        });
    }

    private void updateCustomerInfo() {
        if (customer != null && customerNameLabel != null) {
            customerNameLabel.setText(customer.getFirstName() + " " + customer.getLastName());
        }
    }

    private void loadCustomerAccounts() {
        if (customer == null) {
            showAlert("Error", "No customer information available.");
            return;
        }

        if (accountDAO == null) {
            showAlert("Error", "Database connection not available.");
            return;
        }

        Task<List<Account>> task = new Task<List<Account>>() {
            @Override
            protected List<Account> call() throws Exception {
                return accountDAO.getCustomerAccounts(customer.getCustId());
            }
        };

        task.setOnSucceeded(event -> {
            List<Account> accounts = task.getValue();
            accountTable.getItems().clear();
            if (accounts != null && !accounts.isEmpty()) {
                accountTable.getItems().addAll(accounts);
            } else {
                showAlert("Information", "No accounts found for " + customer.getFirstName() + " " + customer.getLastName());
            }
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            exception.printStackTrace();
            showAlert("Error", "Failed to load accounts: " + exception.getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void handleRefresh() {
        loadCustomerAccounts();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/dashboard-view.fxml"));
            Scene scene = new Scene(loader.load());

            DashboardController dashboardController = loader.getController();
            dashboardController.setCustomer(customer);

            Stage stage = (Stage) accountTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard - " + customer.getFirstName());
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

// Set stage size to match screen
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Cannot return to dashboard: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}