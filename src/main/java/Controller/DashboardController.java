package Controller;

import Model.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import java.io.IOException;

public class DashboardController {

    @FXML private Label welcomeLabel;
    private Customer customer;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null && welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + customer.getFirstName() + "!");
        }
    }

    @FXML
    private void handleViewProfile(ActionEvent event) throws IOException {
        openCustomerProfilePage();
    }

    @FXML
    private void handleViewAccounts(ActionEvent event) throws IOException {
        openPage("/com/katlo/ooadbanksystem2025/AccountListPage.fxml");
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) throws IOException {
        openPage("/com/katlo/ooadbanksystem2025/NewAccountPage.fxml");
    }

    @FXML
    private void handleTransactionHistory(ActionEvent event) throws IOException {
        openPage("/com/katlo/ooadbanksystem2025/TransactionHistoryPage.fxml");
    }

    @FXML
    private void handleTransferFunds(ActionEvent event) throws IOException {
        openPage("/com/katlo/ooadbanksystem2025/TransferFundsPage.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/login-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Bank System Login");
        stage.show();
    }

    private void openPage(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void openCustomerProfilePage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/CustomerProfile.fxml"));
        Scene scene = new Scene(loader.load());
        
        CustomerProfileController controller = loader.getController();
        controller.setCustomer(this.customer);
        
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}