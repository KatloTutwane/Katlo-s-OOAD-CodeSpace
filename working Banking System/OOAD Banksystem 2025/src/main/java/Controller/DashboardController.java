package Controller;

import Model.BankSystem;
import Model.Customer;
import Model.Account;
import Model.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalBalanceLabel;
    @FXML private Label activeAccountsLabel;
    @FXML private Label transactionsLabel;

    private Customer customer;
    private BankSystem bankSystem;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null && welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + customer.getFirstName() + "!");
            updateAccountSummary();
        }
    }

    public void setBankSystem(BankSystem bankSystem) {   // <--- ADDED
        this.bankSystem = bankSystem;
    }

    private void updateAccountSummary() {
        if (customer == null) return;

        List<Account> accounts = customer.getAccounts();
        int activeAccounts = accounts != null ? accounts.size() : 0;
        double totalBalance = accounts != null ?
                accounts.stream().mapToDouble(Account::getBalance).sum() : 0.0;

        if (totalBalanceLabel != null)
            totalBalanceLabel.setText(String.format("$%,.2f", totalBalance));
        if (activeAccountsLabel != null)
            activeAccountsLabel.setText(String.valueOf(activeAccounts));
    }


    @FXML
    private void handleViewProfile(MouseEvent event) throws IOException {
        openCustomerProfilePage();
    }

    @FXML
    private void handleViewAccounts(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/AccountListPage.fxml"));
        Scene scene = new Scene(loader.load());

        AccountListController controller = loader.getController();
        controller.setCustomer(this.customer);

        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(scene);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

// Set stage size to match screen
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.show();
    }

    @FXML
    private void handleCreateAccount(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/NewAccountPage.fxml"));
        Scene scene = new Scene(loader.load());

        NewAccountController controller = loader.getController();
        controller.setCustomer(this.customer);

        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(scene);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

// Set stage size to match screen
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.show();
    }

    @FXML
    private void handleTransactionHistory(MouseEvent event) throws IOException {
        openPage("/com/katlo/ooadbanksystem2025/TransactionHistoryPage.fxml");

    }

    @FXML
    private void handleTransferFunds() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/TransferFundsPage.fxml"));
        Scene scene = new Scene(loader.load());

        TransferFundsController controller = loader.getController();
        controller.setCustomer(customer);

        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(scene);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

// Set stage size to match screen
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.show();
    }


    @FXML
    private void handleLogout(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/login-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("MATSHELONYANA BANK");
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

// Set stage size to match screen
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.show();
    }

    private void openPage(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Scene scene = new Scene(loader.load());

        Object controller = loader.getController();

        // Pass customer to pages that need it
        if (controller instanceof TransactionHistoryController) {
            ((TransactionHistoryController) controller).setCustomer(this.customer);
        } else if (controller instanceof AccountListController) {
            ((AccountListController) controller).setCustomer(this.customer);
        } else if (controller instanceof NewAccountController) {
            ((NewAccountController) controller).setCustomer(this.customer);
        } else if (controller instanceof CustomerProfileController) {
            ((CustomerProfileController) controller).setCustomer(this.customer);
        } else if (controller instanceof TransferFundsController) {              // <--- ADDED
            ((TransferFundsController) controller).setCustomer(this.customer);
        }

        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(scene);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

// Set stage size to match screen
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.show();
    }


    private void openCustomerProfilePage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/CustomerProfile.fxml"));
        Scene scene = new Scene(loader.load());

        CustomerProfileController controller = loader.getController();
        controller.setCustomer(this.customer);

        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(scene);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

// Set stage size to match screen
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.show();
    }
}
