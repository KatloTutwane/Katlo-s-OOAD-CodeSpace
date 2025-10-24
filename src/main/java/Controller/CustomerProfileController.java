package Controller;

import Model.Customer;
import Model.IndividualCustomer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class CustomerProfileController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField nationalIdField;

    private Customer currentCustomer;

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        loadCustomerData();
    }

    private void loadCustomerData() {
        if (currentCustomer == null) return;

        firstNameField.setText(currentCustomer.getFirstName());
        lastNameField.setText(currentCustomer.getLastName());
        emailField.setText(currentCustomer.getEmail());
        addressField.setText(currentCustomer.getAddress());

        if (currentCustomer instanceof IndividualCustomer individual) {
            nationalIdField.setText(individual.getNationalId());
            nationalIdField.setVisible(true);
        } else {
            nationalIdField.setVisible(false);
        }
    }

    @FXML
    private void handleSaveProfile() {
        if (currentCustomer == null) return;

        currentCustomer.setEmail(emailField.getText());
        currentCustomer.setAddress(addressField.getText());

        if (currentCustomer instanceof IndividualCustomer individual) {
            individual.setNationalId(nationalIdField.getText());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Profile Updated");
        alert.setHeaderText(null);
        alert.setContentText("Your profile has been updated successfully!");
        alert.showAndWait();
    }

    @FXML
    private void handleBackToDashboard() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/dashboard-view.fxml"));
        Scene scene = new Scene(loader.load());
        
        DashboardController dashboardController = loader.getController();
        dashboardController.setCustomer(currentCustomer);
        
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}