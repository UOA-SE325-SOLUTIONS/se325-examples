package uoa.se325.auction.model;

import jakarta.persistence.Entity;

/**
 * Entity class to represent bank accounts
 */
@Entity
public class BankAccount extends BillingDetails {

    private String account;
    private String bankName;

    // Default constructor required by JPA.
    public BankAccount() {
    }

    /**
     * Creates a BankAccount object with the account's owner's name, the name
     * of the account and the name of the bank.
     */
    public BankAccount(String owner, String account, String bankName) {
        super(owner);
        this.account = account;
        this.bankName = bankName;
    }

    /**
     * Returns this BankAccount object's account name.
     */
    public String getAccount() {
        return account;
    }

    /**
     * Returns this BankAccount object's bank name.
     *
     * @return
     */
    public String getBankName() {
        return bankName;
    }
}
