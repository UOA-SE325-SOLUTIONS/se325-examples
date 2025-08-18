package uoa.se325.auction.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity class to represent auction bids.
 * <p>
 * A Bid instance is associated with a particular auctionable Item, and stores an amount.
 */
@Entity
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long amount;

    // Define a many-to-one association with Item - many Bids can be associated
    // with a single Item. When a Bid is loaded, its associated Item will be
    // loaded on demand (lazily).
    @ManyToOne(fetch = FetchType.LAZY)
    // Make the association mandatory - a Bid MUST have an associated Item.
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private Item item;

    // Bids MUST be made by a user.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User bidder;

    // When was this bid made?
    private LocalDateTime timestamp;

    // Required by JPA.
    public Bid() {
    }

    public Bid(Item item, User user, long amount) {
        this.item = item;
        this.amount = amount;
        this.bidder = user;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public long getAmount() {
        return amount;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getBidder() {
        return bidder;
    }

    public void setBidder(User bidder) {
        this.bidder = bidder;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bid bid = (Bid) o;
        return Objects.equals(id, bid.id) && Objects.equals(amount, bid.amount)
                && Objects.equals(timestamp, bid.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, timestamp);
    }

    @Override
    public String toString() {
        return "Bid " + id + ": " + amount + " for Item " + item.getId() + " " + item.getName() + " hash: " + hashCode();
    }
}