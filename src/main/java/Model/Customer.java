package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public abstract class Customer {
    protected final String custId;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String address;
    protected String userName;
    protected String password;

    protected final List<Account> accounts = new ArrayList<>();

    public Customer(String firstName, String lastName, String email, String address, String userName, String password) {
        this.custId = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.userName = userName;
        this.password = password;
    }

    public String getCustId() { return custId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }

    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }

    public void addAccount(Account account) {
        if (account != null && !accounts.contains(account)) accounts.add(account);
    }


    public abstract void openAccount(Account account);

    @Override
    public String toString() {
        return String.format("%s %s (%s)", firstName, lastName, getClass().getSimpleName());
    }
}
