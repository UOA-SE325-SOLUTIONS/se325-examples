package se325.example13.parolee.domain;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.util.Objects;

/**
 * Class to represent an Address. Immutable
 */
@Embeddable
public class Address {

    private String streetNumber;

    private String streetName;

    private String suburb;

    private String city;

    private String zipCode;

    @Embedded
    private GeoPosition location;

    protected Address() {
    }

    public Address(String streetNumber,
                   String streetName,
                   String suburb,
                   String city,
                   String zipCode) {
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.suburb = suburb;
        this.city = city;
        this.zipCode = zipCode;
    }

    public Address(String streetNumber,
                   String streetName,
                   String suburb,
                   String city,
                   String zipCode,
                   GeoPosition location) {
        this(streetNumber, streetName, suburb, city, zipCode);
        this.location = location;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getSuburb() {
        return suburb;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public GeoPosition getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(streetNumber, address.streetNumber) && Objects.equals(streetName, address.streetName) && Objects.equals(suburb, address.suburb) && Objects.equals(city, address.city) && Objects.equals(zipCode, address.zipCode) && Objects.equals(location, address.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetNumber, streetName, suburb, city, zipCode);
    }
}
