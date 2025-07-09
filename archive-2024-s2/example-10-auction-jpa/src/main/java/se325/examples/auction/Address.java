package se325.examples.auction;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

/**
 * Embeddable class to represent an Address.
 */
@Embeddable
public class Address {
    @Column(name = "STREET", length = 50, nullable = false)
    private String street;

    @Column(name = "CITY", length = 30, nullable = false)
    private String city;

    @Column(name = "ZIP_CODE", length = 10)
    private String zipCode;

    // Required by JPA.
    protected Address() {
    }

    public Address(String street, String city, String zipCode) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
                Objects.equals(city, address.city) &&
                Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, zipCode);
    }
}
