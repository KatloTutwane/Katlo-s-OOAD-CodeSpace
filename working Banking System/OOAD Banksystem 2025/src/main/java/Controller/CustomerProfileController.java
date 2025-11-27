package Controller;

import Model.Customer;
import Model.IndividualCustomer;
import Model.Company;
import dao.CustomerDAOImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomerProfileController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField nationalIdField;
    @FXML private TextField companyNameField;
    @FXML private TextField companyAddressField;


    private Customer customer;

    // ke batla go dira that fluid name exchange
    public void setCustomer(Customer customer) {
        this.customer = customer;
        populateFields();
    }

    /** ha ke tlhopa which field names i should fill */
    private void populateFields() {
        if (customer == null) return;

        firstNameField.setText(customer.getFirstName());
        lastNameField.setText(customer.getLastName());
        emailField.setText(customer.getEmail());
        addressField.setText(customer.getAddress());

        if (customer instanceof IndividualCustomer individual) {
            nationalIdField.setText(individual.getNationalId());
            nationalIdField.setDisable(false);

            // Disable company fields
            companyNameField.setDisable(true);
            companyAddressField.setDisable(true);
        } else if (customer instanceof Company company) {
            companyNameField.setText(company.getCompanyName());
            companyAddressField.setText(company.getCompanyAddress());

            // Disable individual fields
            nationalIdField.setDisable(true);
        }
    }

    /** Save  */
    @FXML
    private void handleSaveProfile() {
        if (customer == null) return;

        customer.setFirstName(firstNameField.getText());
        customer.setLastName(lastNameField.getText());
        customer.setEmail(emailField.getText());
        customer.setAddress(addressField.getText());

        if (customer instanceof IndividualCustomer individual) {
            individual.setNationalId(nationalIdField.getText());
        } else if (customer instanceof Company company) {
            company.setCompanyName(companyNameField.getText());
            company.setCompanyAddress(companyAddressField.getText());
        }

        // Save to database via DAO
        CustomerDAOImpl dao = new CustomerDAOImpl();
        boolean success = dao.updateCustomer(customer);

        if (success) {
            System.out.println("Profile updated successfully!");
        } else {
            System.err.println("Failed to update profile.");
        }
    }

    /** back like mokokotlo to dashboard, passing customer back */
    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/dashboard-view.fxml"));
            Scene scene = new Scene(loader.load());

            DashboardController dashboardController = loader.getController();
            dashboardController.setCustomer(customer);

            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard - " + customer.getFirstName());
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

// Set stage size to match screen
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
