package se325.example13.parolee.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Class to represent a geographic location in terms of latitude and longitude.
 * GeoPosition objects are immutable.
 */
@Embeddable
@Access(AccessType.FIELD)
public class GeoPosition {

    @Column(nullable = true)
    private double latitude;
    @Column(nullable = true)
    private double longitude;

    protected GeoPosition() {
    }

    /**
     * Creates a new {@link GeoPosition}. Demonstrates how we can tell Jackson to use this constructor rather than any
     * setters when creating new instances. We need this here because GeoPosition objects are immutable (and therefore
     * have no setters).
     *
     * @param lat the latitude
     * @param lng the longitude
     */
    public GeoPosition(double lat, double lng) {
        latitude = lat;
        longitude = lng;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoPosition that = (GeoPosition) o;

        double latDiff = Math.abs(that.latitude - latitude);
        double lngDiff = Math.abs(that.longitude - longitude);
        return latDiff < 1e-10 && lngDiff < 1e-10;
    }
}
