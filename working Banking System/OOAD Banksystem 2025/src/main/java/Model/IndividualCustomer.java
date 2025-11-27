package Model;

public class IndividualCustomer extends Customer {
    private String nationalId;

    // New customer constructor
    public IndividualCustomer(String firstName, String lastName, String email, String address,
                              String userName, String password, String nationalId) {
        super(firstName, lastName, email, address, userName, password);
        this.nationalId = nationalId;
    }

    // DB load constructor
    public IndividualCustomer(String custId, String firstName, String lastName, String email, String address,
                              String userName, String password, String nationalId) {
        super(custId, firstName, lastName, email, address, userName, password);
        this.nationalId = nationalId;
    }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    @Override
    public void openAccount(Account account) {
        addAccount(account);
    }
}
