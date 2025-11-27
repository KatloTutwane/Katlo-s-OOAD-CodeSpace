package Controller;

import Model.BankSystem;
import Model.Customer;
import Model.Transaction;
import Model.Account;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TransactionHistoryController {


    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;
    @FXML private TableColumn<Transaction, String> fromColumn;
    @FXML private TableColumn<Transaction, String> toColumn;
    @FXML private TableColumn<Transaction, String> detailsColumn;

    private Customer customer;
    private BankSystem bankSystem;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        loadTransactions();
    }

    public void setBankSystem(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
    }

    @FXML
    public void initialize() {

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("fromAccountNumber"));
        toColumn.setCellValueFactory(new PropertyValueFactory<>("toAccountNumber"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
    }

    private void loadTransactions() {
        if (customer == null || bankSystem == null) return;

        List<Transaction> allTx = new ArrayList<>();

        // Fetch transactions from database for each customer account
        for (Account account : customer.getAccounts()) {
            List<Transaction> txForAcc = bankSystem.getTransactionsForAccount(account.getAccountNumber());
            allTx.addAll(txForAcc);
        }

        // Sort by date descending
        allTx.sort(Comparator.comparing(Transaction::getDate).reversed());

        transactionTable.setItems(FXCollections.observableArrayList(allTx));
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/dashboard-view.fxml"));
        Scene scene = new Scene(loader.load());

        DashboardController controller = loader.getController();
        controller.setCustomer(customer);

        Stage stage = (Stage) transactionTable.getScene().getWindow();
        stage.setScene(scene);

        // Maximize window
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage.setX(screen.getMinX());
        stage.setY(screen.getMinY());
        stage.setWidth(screen.getWidth());
        stage.setHeight(screen.getHeight());
        stage.show();
    }


}
