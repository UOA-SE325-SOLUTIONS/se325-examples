package se325.examples.auction;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class to define a Category for auctionable Items.
 * <p>
 * Category and Item are related using a many-to-many association. A Category
 * can contain many Items; an Item may be part of many Categories.
 */
@Entity
public class Category {

    private static Logger LOGGER = LoggerFactory.getLogger(Category.class);

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // Define the many-to-many association. The association is implemented
    // using an intermediary join table. Cascading persistence is set so that
    // whenever a Category instance is persisted, so to are its associated
    // Items.
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "CATEGORY_ITEM",
            joinColumns = @JoinColumn(name = "CATEGORY_ID"),
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID"))
    private Set<Item> items = new HashSet<>();

    // Required by JPA.
    protected Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public void addItem(Item item) {
        LOGGER.info("Attempting to add: " + item.toString());
        items.add(item);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Item> getItems() {
        return Collections.unmodifiableSet(items);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item))
            return false;
        if (obj == this)
            return true;

        Category rhs = (Category) obj;
        return new EqualsBuilder().append(id, rhs.getId())
                .append(name, rhs.getName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(getClass().getName())
                .append(id).append(name).toHashCode();
    }
}
