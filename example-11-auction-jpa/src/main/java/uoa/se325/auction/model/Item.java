package uoa.se325.auction.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity class representing auctionable Items. Item instances have a name,
 * collection of images, collection of associated Bids, a buyer (User), and a
 * collection of Categories that the Item appears in.
 */
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    // Set up the collection of image names as a Set. Lazy loaded by default.
    @ElementCollection
    // Another option: Can use eager fetching, which will cause a join operation
//    @ElementCollection(fetch = FetchType.EAGER)
    // Third option: With eager fetching, add SELECT Fetch mode (Hibernate-specific)
    // which will cause a separate query to be run for the item's images, rather than
    // a join operation.
//    @Fetch(FetchMode.SELECT)
    @CollectionTable(name = "IMAGE")
    private Set<Image> images = new HashSet<>();

    // Fetch strategies for bids:
    // Option one: Lazily load bids. When we read ONE item's bids, that single item's bids
    // will be loaded from the database.
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // Option two: Use subselect fetch mode (Hibernate-specific). When we read ONE item's bids,
    // ALL items' bids will be loaded from the database with a single query.
//    @OneToMany(mappedBy = "item", cascade = CascadeType.PERSIST)
//    @Fetch(FetchMode.SUBSELECT)
    // Option three: Use select fetch mode (Hibernate-specific). When we eagerly fetch these bids,
    // we will do a separate query for the item's bids, rather than doing a join operation.
//    @OneToMany(mappedBy = "item", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
//    @Fetch(FetchMode.SELECT)
    private Set<Bid> bids = new HashSet<>();

    // Map the buyer property. Since an Item will not always be associated with
    // a User playing the buyer role, define the mapping using an intermediate
    // join table. This avoids the use of null value columns in the database.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "ITEM_BUYER",
            joinColumns = @JoinColumn(name = "ITEM_ID")
    )
    private User buyer;

    // Lazy loaded by default. Can use any of the fetch strategies mentioned in above comments.
    @ManyToMany(mappedBy = "items")
    private Set<Category> categories = new HashSet<>();

    // Enables optimistic concurrency control on items. Items' bids are constantly being updated
    // so we need to make sure that items have a version number to disallow "out-of-order" bidding.
    @Version
    private Long version;

    // Required by JPA
    public Item() {
    }

    public Item(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Image> getImages() {
        // Wrap the Set of Image objects with a wrapper that provides read-only
        // access. Clients thus can't change the state of the returned Set.
        return Collections.unmodifiableSet(images);
    }

    public User getBuyer() {
        return buyer;
    }

    public Set<Bid> getBids() {
        return bids;
    }

    public Set<Category> getCategories() {
        return Collections.unmodifiableSet(categories);
    }

    public boolean containsBid(Bid bid) {
        return bids.contains(bid);
    }

    public void addImage(Image image) {
        images.add(image);
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) &&
                Objects.equals(name, item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Item " + id + ": " + name + " hash: " + hashCode();
    }
}
