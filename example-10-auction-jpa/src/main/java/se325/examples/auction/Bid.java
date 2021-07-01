package se325.examples.auction;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Entity class to represent auction bids.
 * <p>
 * A Bid instance is associated with a particular auctionable Item, and stores an amount.
 */
@Entity
public class Bid {

    private static Logger LOGGER = LoggerFactory.getLogger(Bid.class);

    @Id
    @GeneratedValue
    protected Long id;

    private BigDecimal amount;

    // Define a many-to-one association with Item - many Bids can be associated
    // with a single Item. When a Bid is loaded, its associated Item will be
    // loaded on demand (lazily).
    @ManyToOne(fetch = FetchType.LAZY)
    // Make the association mandatory - a Bid MUST have an associated Item.
    @JoinColumn(name = "ITEM_ID", nullable = false)
    protected Item item;

    // Required by JPA.
    protected Bid() {
    }

    public Bid(Item item, BigDecimal amount) {
        this.item = item;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Bid))
            return false;
        if (obj == this)
            return true;

        Bid rhs = (Bid) obj;
        return new EqualsBuilder().
                append(id, rhs.getId()).
                append(amount, rhs.getAmount()).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(getClass().getName()).
                append(id).
                append(amount).
                toHashCode();
    }

    @Override
    public String toString() {
        return "Bid " + id + ": " + amount + " for Item " + item.getId() + " " + item.getName() + " hash: " + hashCode();
    }
}