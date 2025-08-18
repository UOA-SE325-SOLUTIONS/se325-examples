package uoa.se325.auction.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Abstract entity superclass representing billing details. This class is
 * intended to be subclassed by concrete types of billings details, including
 * BankAccount and CreditCard.
 */
@Entity

// ===== POSSIBLE INHERITANCE MAPPING STRATEGIES =====
// Inheritance strategy 2: table per concrete class with unions
// @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

// Inheritance strategy 3: table per class hierarchy
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)

// Inheritance strategy 4: table per class with joins
// @Inheritance(strategy = InheritanceType.JOINED)
// ===================================================

// Use the strategy that represents the inheritance hierarchy in the relational schema.
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BillingDetails {

    // Database identity field.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    // Every BillingDetails instance must have a value for the owner field.
    @Column(nullable = false)
    protected String owner;

    // Default constructor required by JPA.
    public BillingDetails() {
    }

    /**
     * Constructor that takes a parameter representing the owner's name.
     */
    public BillingDetails(String owner) {
        this.owner = owner;
    }

    /**
     * Returns this BillingDetails' database identity.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns this BillingDetails' owner's name.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Uses this BillingDetails to make a payment.
     */
    public void pay(BigDecimal amount) {
        // Code to use this BillingDetails object to make a payment.
        System.out.println(owner + " just paid $" + amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillingDetails that = (BillingDetails) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
