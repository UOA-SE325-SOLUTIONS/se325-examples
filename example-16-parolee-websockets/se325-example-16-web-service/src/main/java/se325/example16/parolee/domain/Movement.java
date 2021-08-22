package se325.example16.parolee.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Class to represent a Parolee's movement. A Movement instance stores a
 * timestamp and a latitude/longitude position. Movement objects are immutable.
 */
@Embeddable
@Access(AccessType.FIELD)
public class Movement {

    private LocalDateTime timestamp;

    @Embedded
    private GeoPosition geoPosition;

    protected Movement() {
    }

    /**
     * Creates a new {@link Movement}. Demonstrates how we can tell Jackson to use this constructor rather than any
     * setters when creating new instances. We need this here because Movement objects are immutable (and therefore
     * have no setters).
     *
     * @param timestamp   the time of the movement
     * @param geoPosition the position of the movement
     */
    public Movement(LocalDateTime timestamp, GeoPosition geoPosition) {
        this.timestamp = timestamp;
        this.geoPosition = geoPosition;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public GeoPosition getGeoPosition() {
        return geoPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movement that = (Movement) o;
        return timestamp.isEqual(that.timestamp) && Objects.equals(geoPosition, that.geoPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }
}