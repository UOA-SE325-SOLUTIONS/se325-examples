package se325.examples.auction.onetomany;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "BID_ONETOMANY")
public class Bid_onetomany {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    protected Bid_onetomany() {
    }

    public Bid_onetomany(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Bid_onetomany))
            return false;
        if (obj == this)
            return true;

        Bid_onetomany rhs = (Bid_onetomany) obj;
        return new EqualsBuilder().
                append(id, rhs.getId()).
                append(amount, rhs.getAmount()).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(id).
                append(amount).
                toHashCode();
    }
}
