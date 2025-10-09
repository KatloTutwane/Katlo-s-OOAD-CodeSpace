


public class IndividualCustomer extends Customer {
    private String nationalId;

    public IndividualCustomer(String firstName, String lastName, String email, String address,
                              String userName, String password, String nationalId) {
        super(firstName, lastName, email, address, userName, password);
        this.nationalId = nationalId;
    }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    @Override
    public void openAccount(Account account) {
        System.out.println("Opening personal account for " + firstName);
        addAccount(account);
    }
}
