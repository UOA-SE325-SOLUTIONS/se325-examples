package uoa.se325.auction.model;

import jakarta.persistence.*;

import java.util.*;

/**
 * Entity class to represent a User in an auction application.
 */
@Entity
@Table(name = "USERS")
public class User {
    public enum AddressType {HOME, SHIPPING, BILLING}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USERNAME", nullable = false, length = 30)
    private String username;

    @Column(name = "LASTNAME", nullable = false, length = 30)
    private String lastname;

    @Column(name = "FIRSTNAME", nullable = false, length = 30)
    private String firstname;

    @ManyToOne(fetch = FetchType.LAZY)
    private BillingDetails defaultBilling;

    @Embedded
    private Address homeAddress;

    @OneToMany(mappedBy = "bidder", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Bid> bids = new HashSet<>();

    @AttributeOverrides({
            @AttributeOverride(name = "street",
                    column = @Column(name = "SHIPPING_STREET", nullable = false)),
            @AttributeOverride(name = "city",
                    column = @Column(name = "SHIPPING_CITY", nullable = false)),
            @AttributeOverride(name = "zipCode",
                    column = @Column(name = "SHIPPING_ZIP_CODE")),
    })
    private Address shippingAddress;

    @AttributeOverrides({
            @AttributeOverride(name = "street",
                    column = @Column(name = "BILLING_STREET", nullable = false)),
            @AttributeOverride(name = "city",
                    column = @Column(name = "BILLING_CITY", nullable = false)),
            @AttributeOverride(name = "zipCode",
                    column = @Column(name = "BILLING_ZIP_CODE")),
    })
    private Address billingAddress;

    @Transient
    private Map<AddressType, java.util.function.Supplier<Address>> addressGetters = new HashMap<>();
    @Transient
    private Map<AddressType, java.util.function.Consumer<Address>> addressSetters = new HashMap<>();

    @OneToMany(mappedBy = "buyer")
    private Set<Item> boughtItems = new HashSet<>();

    protected User() {
        this.addressGetters.put(AddressType.BILLING, () -> billingAddress);
        this.addressGetters.put(AddressType.SHIPPING, () -> shippingAddress);
        this.addressGetters.put(AddressType.HOME, () -> homeAddress);
        this.addressSetters.put(AddressType.BILLING, address -> {
            this.billingAddress = address;
        });
        this.addressSetters.put(AddressType.SHIPPING, address -> {
            this.shippingAddress = address;
        });
        this.addressSetters.put(AddressType.HOME, address -> {
            this.homeAddress = address;
        });
    }

    public User(String username, String lastname, String firstname) {
        this();
        this.username = username;
        this.lastname = lastname;
        this.firstname = firstname;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return username;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public Address getAddress(AddressType type) {
        return this.addressGetters.get(type).get();
    }

    public BillingDetails getDefaultBillingDetails() {
        return defaultBilling;
    }

    public void setAddress(AddressType type, Address address) {
        this.addressSetters.get(type).accept(address);
    }

    public void setDefaultBillingDetails(BillingDetails defaultBilling) {
        this.defaultBilling = defaultBilling;
    }

    public void addBoughtItem(Item item) {
        boughtItems.add(item);
    }

    public Set<Bid> getBids() {
        return Collections.unmodifiableSet(bids);
    }

    public void addBid(Bid bid) {
        bids.add(bid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

