package se325.examples.auction;

import javax.persistence.Entity;

/**
 * Entity class to represent credit cards.
 */
@Entity
public class CreditCard extends BillingDetails {

    private String cardNumber;
    private String expiryDate;

    // Default constructor required by JPA.
    protected CreditCard() {
    }

    /**
     * Creates a CreditCard instance with parameters for the CreditCard's
     * owner's name, card number and expiry date.
     */
    public CreditCard(String owner, String cardNumber, String expiryDate) {
        super(owner);
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
    }

    /**
     * Returns this CreditCard's card number.
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Returns this CreditCard's expiry date.
     */
    public String getExpirydate() {
        return expiryDate;
    }

}
