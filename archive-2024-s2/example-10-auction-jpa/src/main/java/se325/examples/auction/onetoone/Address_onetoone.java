package se325.examples.auction.onetoone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Address_onetoone {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "STREET", length = 50, nullable = false)
    private String street;

    @Column(name = "CITY", length = 30, nullable = false)
    private String city;

    @Column(name = "ZIP_CODE", length = 10)
    private String zipCode;

    protected Address_onetoone() {
    }

    public Address_onetoone(String street, String city, String zipCode) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }

    public Long getId() {
        return id;
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
}
