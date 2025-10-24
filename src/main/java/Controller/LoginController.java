package Controller;

import Model.BankSystem;
import Model.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoginController {

    // Login Fields
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;

    // Sign-Up Fields
    @FXML private TextField signupFirstNameField;
    @FXML private TextField signupLastNameField;
    @FXML private TextField signupEmailField;
    @FXML private TextField signupAddressField;
    @FXML private TextField signupUsernameField;
    @FXML private PasswordField signupPasswordField;
    @FXML private TextField signupNationalIdField;

    @FXML private Label statusLabel;

    private final BankSystem bankSystem = new BankSystem();
    private final List<Customer> registeredCustomers = new ArrayList<>();

    @FXML
    public void initialize() {
        //  test user
        Customer test = bankSystem.registerIndividual("Katlo", "User", "katlo@email.com",
                "Gaborone", "katlo", "1234", "N1234567");
        registeredCustomers.add(test);
    }

    //  LOGIN
    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText().trim();

        Optional<Customer> match = registeredCustomers.stream()
                .filter(c -> c.getUserName().equals(username) && c.getPassword().equals(password))
                .findFirst();

        if (match.isPresent()) {
            statusLabel.setText("Login successful!");
            openDashboard(match.get());
        } else {
            statusLabel.setText("Invalid username or password!");
        }
    }

    // --- SIGN UP ---
    @FXML
    private void handleSignUp() {
        String firstName = signupFirstNameField.getText().trim();
        String lastName = signupLastNameField.getText().trim();
        String email = signupEmailField.getText().trim();
        String address = signupAddressField.getText().trim();
        String username = signupUsernameField.getText().trim();
        String password = signupPasswordField.getText().trim();
        String nationalId = signupNationalIdField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all required fields for sign-up.");
            return;
        }

        Customer newCustomer = bankSystem.registerIndividual(firstName, lastName, email, address,
                username, password, nationalId);
        registeredCustomers.add(newCustomer);
        statusLabel.setText("Registration successful! You can now log in.");

        // Clear sign-up fields
        signupFirstNameField.clear();
        signupLastNameField.clear();
        signupEmailField.clear();
        signupAddressField.clear();
        signupUsernameField.clear();
        signupPasswordField.clear();
        signupNationalIdField.clear();
    }

    private void openDashboard(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/dashboard-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) loginUsernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard - " + customer.getFirstName());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
