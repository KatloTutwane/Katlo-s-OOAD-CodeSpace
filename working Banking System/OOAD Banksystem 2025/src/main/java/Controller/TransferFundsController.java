package Controller;

import Model.*;
import database.DatabaseConnection;
import dao.AccountDAO;
import dao.AccountDAOImpl;
import dao.TransactionDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class TransferFundsController {

    @FXML private ComboBox<Account> fromAccountComboBox;
    @FXML private ComboBox<Account> toAccountComboBox;
    @FXML private TextField amountField;

    private Customer customer;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private BankSystem bankSystem;

    // -------------------- Set customer + load accounts --------------------

    public void setCustomer(Customer customer) {
        this.customer = customer;


        try {
            // Only need connection for AccountDAOImpl
            Connection conn = DatabaseConnection.getConnection();
            accountDAO = new AccountDAOImpl(conn);

            // TransactionDAO does not need a connection; it uses DatabaseConnection internally
            transactionDAO = new TransactionDAO();

            // Business logic layer
            bankSystem = new BankSystem(accountDAO, transactionDAO);

            // Load accounts for this customer
            List<Account> accounts = bankSystem.getCustomerAccounts(customer.getCustId());

            fromAccountComboBox.getItems().clear();
            toAccountComboBox.getItems().clear();
            fromAccountComboBox.getItems().addAll(accounts);
            toAccountComboBox.getItems().addAll(accounts);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    // -------------------- Transfer Logic --------------------

    @FXML
    private void handleTransfer() {
        Account from = fromAccountComboBox.getValue();
        Account to = toAccountComboBox.getValue();

        if (from == null || to == null) {
            System.out.println("Please select both accounts!");
            return;
        }

        if (from == to) {
            System.out.println("Cannot transfer to the same account!");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format!");
            return;
        }

        try {
            // NEW — use BankSystem (business logic)
            bankSystem.transfer(from.getAccountNumber(), to.getAccountNumber(), amount);

            DatabaseConnection.commit();
            System.out.println("Transfer successful!");

        } catch (Exception e) {
            DatabaseConnection.rollback();
            System.out.println("Transfer failed: " + e.getMessage());
        }

        // refresh
        setCustomer(customer);
    }

    // -------------------- Back Navigation --------------------

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/dashboard-view.fxml"));
        Scene scene = new Scene(loader.load());

        DashboardController controller = loader.getController();
        controller.setCustomer(customer);

        Stage stage = (Stage) amountField.getScene().getWindow();
        stage.setScene(scene);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());

        stage.show();
    }
}
