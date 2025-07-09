package se325.examples.auction;

import javax.persistence.*;
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
    @GeneratedValue
    private Long id;
    private String name;

    // Set up the collection of image names as a Set.
    @ElementCollection
    @CollectionTable(name = "IMAGE")
    private Set<Image> images = new HashSet<>();

    // Map the collection of Bids. The inverse many-to-one relationship is set
    // up on class Bid's item property.
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Bid> bids = new HashSet<>();

    // Map the buyer property. Since an Item will not always be associated with
    // a User playing the buyer role, define the mapping using an intermediate
    // join table. This avoids the use of null value columns in the database.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "ITEM_BUYER",
            joinColumns = @JoinColumn(name = "ITEM_ID")
    )
    private User buyer;

    @ManyToMany(mappedBy = "items")
    private Set<Category> categories = new HashSet<>();

    protected Item() {
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
        // Wrap the Set of Bid objects with a wrapper that provides read-only
        // access. Clients thus can't change the state of the returned Set.
        return Collections.unmodifiableSet(bids);
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

    public void addBid(Bid bid) {
        bids.add(bid);
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public void addCategory(Category category) {
        categories.add(category);
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
