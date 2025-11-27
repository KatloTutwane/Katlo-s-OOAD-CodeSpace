package dao;

import Model.Customer;
import Model.IndividualCustomer;
import Model.Company;
import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
    // Create
    boolean saveCustomer(Customer customer);
    
    // Read
    Optional<Customer> findCustomerById(String custId);
    Optional<Customer> findCustomerByUserName(String userName);
    Optional<Customer> findCustomerByEmail(String email);
    List<Customer> findAllCustomers();
    List<IndividualCustomer> findAllIndividualCustomers();
    List<Company> findAllCompanyCustomers();
    
    // Update
    boolean updateCustomer(Customer customer);
    
    // Delete
    boolean deleteCustomer(String custId);
    
    // Validation
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    boolean validateCredentials(String userName, String password);
}