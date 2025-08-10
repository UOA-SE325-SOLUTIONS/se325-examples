package uoa.se325.parolees.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Class to represent a Parolee's movement. A Movement instance stores a
 * timestamp and a latitude/longitude position. Movement objects are immutable.
 */
@Embeddable
@Access(AccessType.FIELD)
public class Movement implements Comparable<Movement> {

    private LocalDateTime timestamp;
    private GeoPosition geoPosition;

    protected Movement() {
    }

    /**
     * Creates a new {@link Movement}. Demonstrates how we can tell Jackson to use this constructor rather than any
     * setters when creating new instances. We need this here because Movement objects are immutable (and therefore
     * have no setters).
     *
     * @param timestamp the time of the movement
     * @param geoPosition  the position of the movement
     */
    @JsonCreator
    public Movement(@JsonProperty("timestamp") LocalDateTime timestamp, @JsonProperty("geoPosition") GeoPosition geoPosition) {
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
        return toString().equals(o.toString());
//        Movement movement = (Movement) o;
//        return timestamp.isEqual(movement.timestamp) && Objects.equals(geoPosition, movement.geoPosition);
    }

    @Override
    public int hashCode() {
//        return Objects.hash(timestamp, geoPosition);
        return Objects.hash(toString());
    }

    @Override
    public int compareTo(Movement movement) {
        return timestamp.compareTo(movement.timestamp);
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return geoPosition +
                " @ " +
                timeFormatter.format(timestamp) +
                " on " +
                dateFormatter.format(timestamp);
    }
}