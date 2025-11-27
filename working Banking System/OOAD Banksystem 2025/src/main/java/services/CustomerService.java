package services;

import dao.CustomerDAO;
import dao.CustomerDAOImpl;
import Model.Customer;
import Model.IndividualCustomer;
import Model.Company;
import java.util.List;
import java.util.Optional;

public class CustomerService {
    private CustomerDAO customerDAO;
    
    public CustomerService() {
        this.customerDAO = new CustomerDAOImpl();
    }
    
    // Business logic methods ga re batle 2 user names data inergrity ngwanaka
    public boolean registerCustomer(Customer customer) {
        // Check if username already exists
        if (customerDAO.existsByUserName(customer.getUserName())) {
            throw new IllegalArgumentException("Username already exists: " + customer.getUserName());
        }
        
        // Checks if email already exists
        if (customerDAO.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + customer.getEmail());
        }
        
        return customerDAO.saveCustomer(customer);
    }
    
    public Optional<Customer> login(String userName, String password) {
        if (customerDAO.validateCredentials(userName, password)) {
            return customerDAO.findCustomerByUserName(userName);
        }
        return Optional.empty();
    }
    
    public boolean updateCustomerProfile(Customer customer) {
        return customerDAO.updateCustomer(customer);
    }
    
    public List<Customer> getAllCustomers() {
        return customerDAO.findAllCustomers();
    }
    
    public List<IndividualCustomer> getIndividualCustomers() {
        return customerDAO.findAllIndividualCustomers();
    }
    
    public List<Company> getCompanyCustomers() {
        return customerDAO.findAllCompanyCustomers();
    }
    
    public Optional<Customer> getCustomerById(String custId) {
        return customerDAO.findCustomerById(custId);
    }
    
    public Optional<Customer> getCustomerByUserName(String userName) {
        return customerDAO.findCustomerByUserName(userName);
    }
}