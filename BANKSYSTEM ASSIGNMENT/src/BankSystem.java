

import java.util.*;
import java.util.stream.Collectors;

public class BankSystem {
    private final Map<String, Customer> customersById = new HashMap<>();
    private final Map<String, Account> accountsByNumber = new HashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();

    // opening di accounts
    public IndividualCustomer registerIndividual(String firstName, String lastName, String email,
                                                 String address, String userName, String password, String nationalId) {
        IndividualCustomer c = new IndividualCustomer(firstName, lastName, email, address, userName, password, nationalId);
        customersById.put(c.getCustId(), c);
        return c;
    }

    public Company registerCompany(String companyName, String companyAddress,
                                   String contactFirstName, String contactLastName,
                                   String email, String address, String userName, String password) {
        Company c = new Company(companyName, companyAddress, contactFirstName, contactLastName, email, address, userName, password);
        customersById.put(c.getCustId(), c);
        return c;
    }

    public Optional<Customer> findCustomerById(String id) {
        return Optional.ofNullable(customersById.get(id));
    }

    //Account operations
    public Account openSavingsAccount(Customer owner, String branch, double initialDeposit, double interestRate) {
        SavingsAccount acc = new SavingsAccount(branch, initialDeposit, interestRate);
        owner.openAccount(acc);
        accountsByNumber.put(acc.getAccountNumber(), acc);
        recordTransaction(new Transaction(TransactionType.DEPOSIT, null, acc.getAccountNumber(), initialDeposit, "Initial deposit"));
        return acc;
    }

    public Account openChequeAccount(Customer owner, String branch, double initialDeposit, double overdraftLimit) {
        ChequeAccount acc = new ChequeAccount(branch, initialDeposit, overdraftLimit);
        owner.openAccount(acc);
        accountsByNumber.put(acc.getAccountNumber(), acc);
        recordTransaction(new Transaction(TransactionType.DEPOSIT, null, acc.getAccountNumber(), initialDeposit, "Initial deposit"));
        return acc;
    }

    // --- Transactions ---
    public Transaction deposit(String toAccountNumber, double amount) {
        Account to = accountsByNumber.get(toAccountNumber);
        if (to == null) throw new IllegalArgumentException("Account not found");
        to.deposit(amount);
        Transaction tx = new Transaction(TransactionType.DEPOSIT, null, toAccountNumber, amount, "Deposit");
        recordTransaction(tx);
        return tx;
    }

    public Transaction withdraw(String fromAccountNumber, double amount) throws InsufficientFundsException {
        Account from = accountsByNumber.get(fromAccountNumber);
        if (from == null) throw new IllegalArgumentException("Account not found");
        if (!(from instanceof Withdrawable)) throw new IllegalArgumentException("Account does not support withdrawal");
        ((Withdrawable) from).withdraw(amount);
        Transaction tx = new Transaction(TransactionType.WITHDRAWAL, fromAccountNumber, null, amount, "Withdrawal");
        recordTransaction(tx);
        return tx;
    }

    public Transaction transfer(String fromAccountNumber, String toAccountNumber, double amount) throws InsufficientFundsException {
        if (fromAccountNumber.equals(toAccountNumber)) throw new IllegalArgumentException("Cannot transfer to same account");
        Account from = accountsByNumber.get(fromAccountNumber);
        Account to = accountsByNumber.get(toAccountNumber);
        if (from == null || to == null) throw new IllegalArgumentException("Account not found");
        if (!(from instanceof Withdrawable)) throw new IllegalArgumentException("Source account does not support withdrawal");
        ((Withdrawable) from).withdraw(amount);
        to.deposit(amount);
        Transaction tx = new Transaction(TransactionType.TRANSFER, fromAccountNumber, toAccountNumber, amount, "Transfer");
        recordTransaction(tx);
        return tx;
    }

    private void recordTransaction(Transaction tx) {
        transactions.add(tx);
    }

    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        return transactions.stream()
                .filter(t -> accountNumber.equals(t.getFromAccountNumber()) || accountNumber.equals(t.getToAccountNumber()))
                .collect(Collectors.toList());
    }
}
