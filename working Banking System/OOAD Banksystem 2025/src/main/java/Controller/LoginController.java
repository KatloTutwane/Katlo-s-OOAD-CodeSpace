package Controller;

import Model.BankSystem;
import Model.Customer;
import Model.IndividualCustomer;
import dao.AccountDAO;
import dao.AccountDAOImpl;
import dao.CustomerDAOImpl;
import dao.TransactionDAO;
import database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.io.IOException;
import java.sql.Connection;
import java.util.Optional;

public class LoginController {

    // --- Login fields ---
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;

    // --- Registration fields ---
    @FXML private RadioButton individualRadio;
    @FXML private RadioButton companyRadio;

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField nationalIdField;

    @FXML private TextField companyNameField;
    @FXML private TextField companyAddressField;
    @FXML private TextField contactFirstNameField;
    @FXML private TextField contactLastNameField;

    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML private Label statusLabel;

    // CORE SYSTEM
    private BankSystem bankSystem;

    // DAO for login + registration
    private final CustomerDAOImpl customerDAO = new CustomerDAOImpl();

    private ToggleGroup userTypeToggleGroup;

    @FXML
    public void initialize() {

        // --- Setup radio toggle for Individual vs Company ---
        userTypeToggleGroup = new ToggleGroup();
        individualRadio.setToggleGroup(userTypeToggleGroup);
        companyRadio.setToggleGroup(userTypeToggleGroup);
        individualRadio.setSelected(true);
        toggleSignUpFields("Individual");

        userTypeToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                individualRadio.setSelected(true);
                toggleSignUpFields("Individual");
                return;
            }
            toggleSignUpFields(individualRadio.isSelected() ? "Individual" : "Company");
        });

        try {

            // --- SHARED CONNECTION FOR ALL DAOs ---
            Connection conn = DatabaseConnection.getConnection();

            AccountDAO accountDAO = new AccountDAOImpl(conn);
            TransactionDAO transactionDAO = new TransactionDAO(conn);

            // --- Create unified business system ---
            bankSystem = new BankSystem(accountDAO, transactionDAO);

            // --- Optional test user (only if not already saved) ---
            IndividualCustomer test = bankSystem.registerIndividual(
                    "Katlo", "User", "katlo@email.com",
                    "Gaborone", "katlo", "1234", "N1234567"
            );

            if (!customerDAO.existsByUserName(test.getUserName())) {
                customerDAO.saveCustomer(test);
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error initializing system.");
        }
    }

    // LOGIN

    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        boolean isValid = customerDAO.validateCredentials(username, password);

        if (isValid) {
            Optional<Customer> customerOpt = customerDAO.findCustomerByUserName(username);

            customerOpt.ifPresentOrElse(
                    customer -> {
                        statusLabel.setText("Login successful!");
                        openDashboard(customer);
                    },
                    () -> statusLabel.setText("Login succeeded but customer not found.")
            );
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }


    // SIGN UP

    @FXML
    private void handleSignUp() {
        String type = individualRadio.isSelected() ? "Individual" : "Company";

        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in username and password.");
            return;
        }
        if (customerDAO.existsByUserName(username)) {
            statusLabel.setText("Username already exists.");
            return;
        }
        if (!email.isEmpty() && customerDAO.existsByEmail(email)) {
            statusLabel.setText("Email already registered.");
            return;
        }

        Customer newCustomer = null;

        if ("Individual".equals(type)) {
            String first = firstNameField.getText().trim();
            String last = lastNameField.getText().trim();
            String nationalId = nationalIdField.getText().trim();

            if (first.isEmpty() || last.isEmpty() || nationalId.isEmpty()) {
                statusLabel.setText("Fill in all individual fields.");
                return;
            }

            newCustomer = bankSystem.registerIndividual(first, last, email, address, username, password, nationalId);

        } else {
            String compName = companyNameField.getText().trim();
            String compAddr = companyAddressField.getText().trim();
            String contFirst = contactFirstNameField.getText().trim();
            String contLast = contactLastNameField.getText().trim();

            if (compName.isEmpty() || compAddr.isEmpty() || contFirst.isEmpty() || contLast.isEmpty()) {
                statusLabel.setText("Fill in all company fields.");
                return;
            }

            newCustomer = bankSystem.registerCompany(compName, compAddr, contFirst, contLast, email, address, username, password);
        }

        boolean saved = customerDAO.saveCustomer(newCustomer);
        statusLabel.setText(saved ? "Registration successful!" : "Registration failed.");

        if (saved) clearSignUpFields();
    }

    // ---------------------------------------------------------
    // UI helpers
    // ---------------------------------------------------------
    private void toggleSignUpFields(String type) {
        boolean isIndividual = type.equals("Individual");

        firstNameField.setVisible(isIndividual);
        lastNameField.setVisible(isIndividual);
        nationalIdField.setVisible(isIndividual);

        companyNameField.setVisible(!isIndividual);
        companyAddressField.setVisible(!isIndividual);
        contactFirstNameField.setVisible(!isIndividual);
        contactLastNameField.setVisible(!isIndividual);
    }

    private void clearSignUpFields() {
        individualRadio.setSelected(true);
        firstNameField.clear();
        lastNameField.clear();
        nationalIdField.clear();
        companyNameField.clear();
        companyAddressField.clear();
        contactFirstNameField.clear();
        contactLastNameField.clear();
        emailField.clear();
        addressField.clear();
        usernameField.clear();
        passwordField.clear();
    }

    // ---------------------------------------------------------
    // DASHBOARD LOADER
    // ---------------------------------------------------------
    private void openDashboard(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/dashboard-view.fxml"));
            Scene scene = new Scene(loader.load());

            Object controller = loader.getController();
            if (controller instanceof Controller.DashboardController dc) {
                dc.setCustomer(customer);
                dc.setBankSystem(bankSystem);  // <--- IMPORTANT FIX
            }

            Stage stage = (Stage) loginUsernameField.getScene().getWindow();
            stage.setScene(scene);

            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            stage.setMaximized(true);

            stage.setTitle("Dashboard - " +
                    (customer.getFirstName() != null ? customer.getFirstName() : customer.getCustId()));

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error loading dashboard.");
        }
    }
}

