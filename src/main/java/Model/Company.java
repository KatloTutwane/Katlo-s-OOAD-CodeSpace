package Model;

public class Company extends Customer {
    private String companyName;
    private String companyAddress;

    public Company(String companyName, String companyAddress,
                   String contactFirstName, String contactLastName,
                   String email, String address, String userName, String password) {
        super(contactFirstName, contactLastName, email, address, userName, password);
        this.companyName = companyName;
        this.companyAddress = companyAddress;
    }

    public String getCompanyName() { return companyName; }
    public String getCompanyAddress() { return companyAddress; }

    @Override
    public void openAccount(Account account) {
        System.out.println("Opening company account for " + companyName);
        addAccount(account);
    }

    @Override
    public String toString() {
        return String.format("Company: %s (Contact: %s %s)", companyName, firstName, lastName);
    }
}
