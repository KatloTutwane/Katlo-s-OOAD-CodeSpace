package dao;

import Model.Customer;
import Model.IndividualCustomer;
import Model.Company;
import database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public boolean saveCustomer(Customer customer) {
        String sql = """
            INSERT INTO customers (cust_id, customer_type, first_name, last_name, email, 
                                  address, user_name, password, national_id, company_name, company_address)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, customer.getCustId());
            stmt.setString(2, getCustomerType(customer));
            stmt.setString(3, customer.getFirstName());
            stmt.setString(4, customer.getLastName());
            stmt.setString(5, customer.getEmail());
            stmt.setString(6, customer.getAddress());
            stmt.setString(7, customer.getUserName());
            stmt.setString(8, customer.getPassword());

            if (customer instanceof IndividualCustomer individual) {
                stmt.setString(9, individual.getNationalId());
                stmt.setNull(10, Types.VARCHAR);
                stmt.setNull(11, Types.VARCHAR);
            } else if (customer instanceof Company company) {
                stmt.setNull(9, Types.VARCHAR);
                stmt.setString(10, company.getCompanyName());
                stmt.setString(11, company.getCompanyAddress());
            } else {
                stmt.setNull(9, Types.VARCHAR);
                stmt.setNull(10, Types.VARCHAR);
                stmt.setNull(11, Types.VARCHAR);
            }

            int rowsAffected = stmt.executeUpdate();
            DatabaseConnection.commit();
            return rowsAffected > 0;

        } catch (SQLException e) {
            DatabaseConnection.rollback();
            System.err.println("Error saving customer: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Customer> findCustomerById(String custId) {
        String sql = "SELECT * FROM customers WHERE cust_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, custId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding customer by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findCustomerByUserName(String userName) {
        String sql = "SELECT * FROM customers WHERE user_name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding customer by username: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findCustomerByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding customer by email: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Customer> findAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY date_created DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all customers: " + e.getMessage());
        }
        return customers;
    }

    @Override
    public List<IndividualCustomer> findAllIndividualCustomers() {
        List<IndividualCustomer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE customer_type = 'INDIVIDUAL' ORDER BY first_name, last_name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                if (customer instanceof IndividualCustomer individual) {
                    customers.add(individual);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding individual customers: " + e.getMessage());
        }
        return customers;
    }

    @Override
    public List<Company> findAllCompanyCustomers() {
        List<Company> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE customer_type = 'COMPANY' ORDER BY company_name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                if (customer instanceof Company company) {
                    customers.add(company);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding company customers: " + e.getMessage());
        }
        return customers;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        String sql = """
            UPDATE customers 
            SET first_name = ?, last_name = ?, email = ?, address = ?, 
                user_name = ?, password = ?, national_id = ?, company_name = ?, company_address = ?
            WHERE cust_id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getUserName());
            stmt.setString(6, customer.getPassword());

            if (customer instanceof IndividualCustomer individual) {
                stmt.setString(7, individual.getNationalId());
                stmt.setNull(8, Types.VARCHAR);
                stmt.setNull(9, Types.VARCHAR);
            } else if (customer instanceof Company company) {
                stmt.setNull(7, Types.VARCHAR);
                stmt.setString(8, company.getCompanyName());
                stmt.setString(9, company.getCompanyAddress());
            } else {
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.VARCHAR);
                stmt.setNull(9, Types.VARCHAR);
            }

            stmt.setString(10, customer.getCustId());
            int rowsAffected = stmt.executeUpdate();
            DatabaseConnection.commit();
            return rowsAffected > 0;

        } catch (SQLException e) {
            DatabaseConnection.rollback();
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(String custId) {
        String sql = "DELETE FROM customers WHERE cust_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, custId);
            int rowsAffected = stmt.executeUpdate();
            DatabaseConnection.commit();
            return rowsAffected > 0;

        } catch (SQLException e) {
            DatabaseConnection.rollback();
            System.err.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existsByUserName(String userName) {
        String sql = "SELECT COUNT(*) FROM customers WHERE user_name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM customers WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean validateCredentials(String userName, String password) {
        String sql = "SELECT COUNT(*) FROM customers WHERE user_name = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userName);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating credentials: " + e.getMessage());
        }
        return false;
    }

    //  Helper Methods

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        String customerType = rs.getString("customer_type");
        String custId = rs.getString("cust_id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");
        String address = rs.getString("address");
        String userName = rs.getString("user_name");
        String password = rs.getString("password");

        if ("INDIVIDUAL".equals(customerType)) {
            String nationalId = rs.getString("national_id");
            return new IndividualCustomer(custId, firstName, lastName, email, address, userName, password, nationalId);
        } else if ("COMPANY".equals(customerType)) {
            String companyName = rs.getString("company_name");
            String companyAddress = rs.getString("company_address");
            return new Company(custId, companyName, companyAddress, firstName, lastName, email, address, userName, password);
        }

        throw new SQLException("Unknown customer type: " + customerType);
    }


    private String getCustomerType(Customer customer) {
        if (customer instanceof IndividualCustomer) {
            return "INDIVIDUAL";
        } else if (customer instanceof Company) {
            return "COMPANY";
        } else {
            return "UNKNOWN";
        }
    }
}
