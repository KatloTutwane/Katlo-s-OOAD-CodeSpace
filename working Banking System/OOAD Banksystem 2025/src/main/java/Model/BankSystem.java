package Model;

import dao.AccountDAO;
import dao.TransactionDAO;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class BankSystem {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private static final Logger logger = Logger.getLogger(BankSystem.class.getName());


    public BankSystem(AccountDAO accountDAO, TransactionDAO transactionDAO) {
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
    }

//  Customer registration

    public IndividualCustomer registerIndividual(String firstName, String lastName, String email,
                                                 String address, String userName, String password, String nationalId) {
        IndividualCustomer customer = new IndividualCustomer(firstName, lastName, email, address, userName, password, nationalId);

        return customer;
    }

    public Company registerCompany(String companyName, String companyAddress,
                                   String contactFirstName, String contactLastName,
                                   String email, String address, String userName, String password) {
        Company company = new Company(companyName, companyAddress, contactFirstName, contactLastName, email, address, userName, password);

        return company;
    }



    public Account openSavingsAccount(String customerId, String branch, double initialDeposit) throws Exception {
        return accountDAO.openSavingsAccount(customerId, branch, initialDeposit);
    }

    public Account openChequeAccount(String customerId, String branch, double initialDeposit, double overdraftLimit) throws Exception {
        return accountDAO.openChequeAccount(customerId, branch, initialDeposit, overdraftLimit);
    }

    public Account openInvestmentAccount(String customerId, String branch, double initialDeposit, String maturityDate) throws Exception {
        return accountDAO.openInvestmentAccount(customerId, branch, initialDeposit, maturityDate);
    }

// -------------------- Transactions (business logic lives here) --------------------

    public Transaction deposit(String toAccountNumber, double amount) throws Exception {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive");

        Optional<Account> toAccountOpt = accountDAO.findAccount(toAccountNumber);
        Account toAccount = toAccountOpt.orElseThrow(() -> new IllegalArgumentException("Account not found: " + toAccountNumber));

        // Apply deposit on model object
        toAccount.deposit(amount);

        // Persist new balance
        accountDAO.updateBalance(toAccountNumber, toAccount.getBalance());

        // Persist transaction
        Transaction tx = new Transaction(TransactionType.DEPOSIT, null, toAccountNumber, amount, "Deposit");
        boolean saved = transactionDAO.createTransaction(tx);
        if (!saved) throw new Exception("Failed to save transaction");

        logger.info("Deposited " + amount + " to " + toAccountNumber);
        return tx;
    }

    public Transaction withdraw(String fromAccountNumber, double amount) throws Exception {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive");

        Optional<Account> fromAccountOpt = accountDAO.findAccount(fromAccountNumber);
        Account fromAccount = fromAccountOpt.orElseThrow(() -> new IllegalArgumentException("Account not found: " + fromAccountNumber));

        if (!(fromAccount instanceof Withdrawable)) throw new IllegalArgumentException("Account does not support withdrawals: " + fromAccountNumber);

        ((Withdrawable) fromAccount).withdraw(amount);

        accountDAO.updateBalance(fromAccountNumber, fromAccount.getBalance());

        Transaction tx = new Transaction(TransactionType.WITHDRAWAL, fromAccountNumber, null, amount, "Withdrawal");
        boolean saved = transactionDAO.createTransaction(tx);
        if (!saved) throw new Exception("Failed to save transaction");

        logger.info("Withdrew " + amount + " from " + fromAccountNumber);
        return tx;
    }

    public Transaction transfer(String fromAccountNumber, String toAccountNumber, double amount) throws Exception {
        if (fromAccountNumber.equals(toAccountNumber)) throw new IllegalArgumentException("Cannot transfer to same account");
        if (amount <= 0) throw new IllegalArgumentException("Transfer amount must be positive");

        Optional<Account> fromAccountOpt = accountDAO.findAccount(fromAccountNumber);
        Account fromAccount = fromAccountOpt.orElseThrow(() -> new IllegalArgumentException("Source account not found: " + fromAccountNumber));

        Optional<Account> toAccountOpt = accountDAO.findAccount(toAccountNumber);
        Account toAccount = toAccountOpt.orElseThrow(() -> new IllegalArgumentException("Target account not found: " + toAccountNumber));

        if (!(fromAccount instanceof Withdrawable)) throw new IllegalArgumentException("Source account does not support withdrawals: " + fromAccountNumber);

        // Withdraw from source
        ((Withdrawable) fromAccount).withdraw(amount);

        // Deposit to target
        toAccount.deposit(amount);

        // Persist both balances
        accountDAO.updateBalance(fromAccountNumber, fromAccount.getBalance());
        accountDAO.updateBalance(toAccountNumber, toAccount.getBalance());

        // Persist transaction
        Transaction tx = new Transaction(TransactionType.TRANSFER, fromAccountNumber, toAccountNumber, amount, "Transfer");
        boolean saved = transactionDAO.createTransaction(tx);
        if (!saved) throw new Exception("Failed to save transaction");

        logger.info(String.format("Transferred %.2f from %s to %s", amount, fromAccountNumber, toAccountNumber));
        return tx;
    }

// -------------------- Interest methods (delegates to DAO) --------------------

    public void applyMonthlyInterest() throws Exception {
        // Business policy of scheduling / when to call resides here.
        accountDAO.applyMonthlyInterest();
    }

    public void applyInterestToAccount(String accountNumber) throws Exception {
        accountDAO.applyInterestToAccount(accountNumber);
    }

// -------------------- Read helpers --------------------

    public List<Account> getCustomerAccounts(String customerId) throws Exception {
        return accountDAO.getCustomerAccounts(customerId);
    }

    public Optional<Account> findAccount(String accountNumber) throws Exception {
        return accountDAO.findAccount(accountNumber);
    }

    public double getCustomerTotalBalance(String customerId) throws Exception {
        return accountDAO.getCustomerTotalBalance(customerId);
    }

// -------------------- Transaction history --------------------

    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        return transactionDAO.getTransactionsByAccount(accountNumber);
    }

    public List<Transaction> getRecentTransactions(String accountNumber, int limit) {
        return transactionDAO.getRecentTransactionsByAccount(accountNumber, limit);
    }


}