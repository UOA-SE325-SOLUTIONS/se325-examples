package se325.example16.parolee.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GeoPositionDTO {

    private final double latitude;
    private final double longitude;

    @JsonCreator
    public GeoPositionDTO(@JsonProperty("latitude") double latitude, @JsonProperty("longitude") double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "GeoPositionDTO{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoPositionDTO that = (GeoPositionDTO) o;
        return Math.abs(latitude - that.latitude) < 1e-10 &&
                Math.abs(longitude - that.longitude) < 1e-10;
    }
}
