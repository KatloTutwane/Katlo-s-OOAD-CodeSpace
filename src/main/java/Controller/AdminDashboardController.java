package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private void handleViewCustomers(ActionEvent event) throws IOException {
        openPage("/com/katlo/ooadbanksystem2025/ViewAllCustomers.fxml", event);
    }

    @FXML
    private void handleManageAccounts(ActionEvent event) throws IOException {
        openPage("/com/katlo/ooadbanksystem2025/ManageAccounts.fxml", event);
    }

    @FXML
    private void handleViewTransactions(ActionEvent event) throws IOException {
        openPage("/com/katlo/ooadbanksystem2025/ViewTransactions.fxml", event);
    }

    @FXML
    private void handleStatistics(ActionEvent event) {
        System.out.println("Statistics feature not implemented yet");
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/katlo/ooadbanksystem2025/login-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Bank System Login");
        stage.show();
    }

    private void openPage(String fxmlPath, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
