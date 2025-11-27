package Controller;

import Model.Account;
import Model.Customer;
import dao.AccountDAO;
import dao.AccountDAOImpl;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;

public class NewAccountController {

    @FXML private ComboBox<String> accountTypeComboBox;
    @FXML private TextField initialDepositField;
    @FXML private Label statusLabel;

    private AccountDAO accountDAO;
    private Customer customer; // The logged-in customer

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @FXML
    private void initialize() {
        accountTypeComboBox.getItems().addAll("Savings", "Cheque", "Investment");

        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521/XEPDB1", "bank_user", "bank123"
            );
            accountDAO = new AccountDAOImpl(conn);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to connect to database!");
        }
    }

    @FXML
    private void handleCreateAccount() {
        if (customer == null) {
            statusLabel.setText("No customer logged in!");
            return;
        }

        String accountType = accountTypeComboBox.getValue();
        String depositText = initialDepositField.getText();

        if (accountType == null || depositText.isEmpty()) {
            statusLabel.setText("Please select account type and enter deposit");
            return;
        }

        double initialDeposit;
        try {
            initialDeposit = Double.parseDouble(depositText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid deposit amount");
            return;
        }

        statusLabel.setText("Creating " + accountType + " account...");

        Task<Account> task = new Task<>() {
            @Override
            protected Account call() throws Exception {
                switch (accountType) {
                    case "Savings":
                        return accountDAO.openSavingsAccount(customer.getCustId(), "Main Branch", initialDeposit);
                    case "Cheque":
                        return accountDAO.openChequeAccount(customer.getCustId(), "Main Branch", initialDeposit, 1000);
                    case "Investment":
                        return accountDAO.openInvestmentAccount(customer.getCustId(), "Main Branch", initialDeposit, "2025-12-31");
                    default:
                        throw new IllegalArgumentException("Unknown account type");
                }
            }
        };

        task.setOnSucceeded(event -> {
            Account created = task.getValue();
            statusLabel.setText(accountType + " account created successfully! Account #: " + created.getAccountNumber());
        });

        task.setOnFailed(event -> {
            Throwable e = task.getException();
            statusLabel.setText("Failed to create account: " + e.getMessage());
            e.printStackTrace();
        });

        new Thread(task).start();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/dashboard-view.fxml"));
            Scene scene = new Scene(loader.load());

            DashboardController controller = loader.getController();
            controller.setCustomer(customer); // Pass customer back to dashboard

            Stage stage = (Stage) initialDepositField.getScene().getWindow();
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
            statusLabel.setText("Failed to go back!");
        }
    }
}
