package se325.examples.auction.onetoone;

import javax.persistence.*;


@Entity
public class User_onetoone {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "USERNAME", nullable = false, length = 30)
    private String username;

    @Column(name = "LASTNAME", nullable = false, length = 30)
    private String lastname;

    @Column(name = "FIRSTNAME", nullable = false, length = 30)
    private String firstname;

    @OneToOne(
            optional = false,
            cascade = CascadeType.PERSIST)
    @JoinColumn(unique = true)
    private Address_onetoone shippingAddress;

    protected User_onetoone() {
    }

    public User_onetoone(String username, String lastname, String firstname) {
        this.username = username;
        this.lastname = lastname;
        this.firstname = firstname;
    }

    public long getId() {
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

    public Address_onetoone getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address_onetoone shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
