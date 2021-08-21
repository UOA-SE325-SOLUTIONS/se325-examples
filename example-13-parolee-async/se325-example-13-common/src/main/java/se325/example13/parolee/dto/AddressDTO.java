package se325.example13.parolee.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class AddressDTO {

    private final String streetNumber;
    private final String streetName;
    private final String suburb;
    private final String city;
    private final String zipCode;
    private final GeoPositionDTO location;

    @JsonCreator
    public AddressDTO(@JsonProperty("streetNumber") String streetNumber,
                      @JsonProperty("streetName") String streetName,
                      @JsonProperty("suburb") String suburb,
                      @JsonProperty("city") String city,
                      @JsonProperty("zipCode") String zipCode,
                      @JsonProperty("location") GeoPositionDTO location) {
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.suburb = suburb;
        this.city = city;
        this.zipCode = zipCode;
        this.location = location;
    }

    public AddressDTO(String streetNumber, String streetName, String suburb, String city, String zipCode) {
        this(streetNumber, streetName, suburb, city, zipCode, null);
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

    public GeoPositionDTO getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressDTO that = (AddressDTO) o;
        return Objects.equals(streetNumber, that.streetNumber) && Objects.equals(streetName, that.streetName) && Objects.equals(suburb, that.suburb) && Objects.equals(city, that.city) && Objects.equals(zipCode, that.zipCode) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetNumber, streetName, suburb, city, zipCode);
    }
}
