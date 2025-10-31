SELECT USER FROM DUAL;



-- DROP TABLES IF THEY EXIST

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE transactions CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE accounts CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE customers CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/


-- CREATE TABLES


-- Customers table
CREATE TABLE customers (
                           cust_id VARCHAR2(36) PRIMARY KEY,
                           customer_type VARCHAR2(20) NOT NULL CHECK (customer_type IN ('INDIVIDUAL', 'COMPANY')),
                           first_name VARCHAR2(100) NOT NULL,
                           last_name VARCHAR2(100) NOT NULL,
                           email VARCHAR2(100) UNIQUE NOT NULL,
                           address VARCHAR2(200),
                           user_name VARCHAR2(50) UNIQUE NOT NULL,
                           password VARCHAR2(100) NOT NULL,
                           national_id VARCHAR2(20),
                           company_name VARCHAR2(200),
                           company_address VARCHAR2(200),
                           date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Accounts table
CREATE TABLE accounts (
                          account_number VARCHAR2(36) PRIMARY KEY,
                          account_type VARCHAR2(20) NOT NULL CHECK (account_type IN ('SAVINGS', 'CHEQUE', 'INVESTMENT')),
                          branch VARCHAR2(100) NOT NULL,
                          balance NUMBER(15,2) DEFAULT 0.00 NOT NULL,
                          cust_id VARCHAR2(36) NOT NULL,
                          interest_rate NUMBER(5,4),
                          overdraft_limit NUMBER(15,2),
                          maturity_date VARCHAR2(20),
                          status VARCHAR2(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CLOSED')),
                          created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (cust_id) REFERENCES customers(cust_id)
);

-- Transactions table
CREATE TABLE transactions (
                              transaction_id VARCHAR2(36) PRIMARY KEY,
                              transaction_type VARCHAR2(20) NOT NULL CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
                              from_account_number VARCHAR2(36),
                              to_account_number VARCHAR2(36),
                              amount NUMBER(15,2) NOT NULL,
                              transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              details VARCHAR2(200),
                              FOREIGN KEY (from_account_number) REFERENCES accounts(account_number),
                              FOREIGN KEY (to_account_number) REFERENCES accounts(account_number),
                              CONSTRAINT chk_transaction_accounts CHECK (
                                  from_account_number IS NOT NULL OR to_account_number IS NOT NULL
                                  )
);


-- INSERT SAMPLE DATA


-- Customers
INSERT INTO customers (cust_id, customer_type, first_name, last_name, email, address, user_name, password, national_id)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'INDIVIDUAL', 'John', 'Smith', 'john.smith@email.com', '123 Main St', 'jsmith', 'password123', 'NAT123456');

INSERT INTO customers (cust_id, customer_type, first_name, last_name, email, address, user_name, password, company_name, company_address)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'COMPANY', 'Jane', 'Doe', 'jane.doe@company.com', '456 Oak Ave', 'jdoe', 'password456', 'Tech Corp', '789 Business Park');

-- Accounts
INSERT INTO accounts (account_number, account_type, branch, balance, cust_id, interest_rate)
VALUES ('acc-sav-001', 'SAVINGS', 'Main Branch', 1000.00, '550e8400-e29b-41d4-a716-446655440000', 0.025);

INSERT INTO accounts (account_number, account_type, branch, balance, cust_id, overdraft_limit)
VALUES ('acc-chq-001', 'CHEQUE', 'Downtown Branch', 500.00, '550e8400-e29b-41d4-a716-446655440001', 1000.00);

-- Transactions
INSERT INTO transactions (transaction_id, transaction_type, from_account_number, to_account_number, amount, details)
VALUES ('tx-001', 'DEPOSIT', NULL, 'acc-sav-001', 1000.00, 'Initial deposit');

INSERT INTO transactions (transaction_id, transaction_type, from_account_number, to_account_number, amount, details)
VALUES ('tx-002', 'WITHDRAWAL', 'acc-chq-001', NULL, 200.00, 'ATM withdrawal');

COMMIT;


-- VERIFY TABLES AND DATA

SELECT table_name FROM user_tables;

SELECT 'Customers: ' || COUNT(*) FROM customers
UNION ALL
SELECT 'Accounts: ' || COUNT(*) FROM accounts
UNION ALL
SELECT 'Transactions: ' || COUNT(*) FROM transactions;

SELECT USER FROM DUAL;
