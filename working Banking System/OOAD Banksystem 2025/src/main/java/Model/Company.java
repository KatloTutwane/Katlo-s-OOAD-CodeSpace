package Model;

public class Company extends Customer {
    private String companyName;
    private String companyAddress;

    // New customer constructor
    public Company(String companyName, String companyAddress, String firstName, String lastName,
                   String email, String address, String userName, String password) {
        super(firstName, lastName, email, address, userName, password);
        this.companyName = companyName;
        this.companyAddress = companyAddress;
    }

    // DB load constructor
    public Company(String custId, String companyName, String companyAddress, String firstName, String lastName,
                   String email, String address, String userName, String password) {
        super(custId, firstName, lastName, email, address, userName, password);
        this.companyName = companyName;
        this.companyAddress = companyAddress;
    }

    public String getCompanyName() { return companyName; }
    public String getCompanyAddress() { return companyAddress; }

    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }

    @Override
    public void openAccount(Account account) {
        addAccount(account);
    }
}
