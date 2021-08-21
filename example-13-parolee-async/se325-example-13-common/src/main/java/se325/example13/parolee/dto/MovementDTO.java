package se325.example13.parolee.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se325.example13.parolee.jackson.LocalDateTimeDeserializer;
import se325.example13.parolee.jackson.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class MovementDTO {

    private final LocalDateTime timestamp;
    private final GeoPositionDTO position;

    @JsonCreator
    public MovementDTO(@JsonProperty("timestamp") LocalDateTime timestamp,
                       @JsonProperty("position") GeoPositionDTO position) {
        this.timestamp = timestamp;
        this.position = position;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public GeoPositionDTO getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "MovementDTO{" +
                "timestamp=" + timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) +
                ", position=" + position +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovementDTO that = (MovementDTO) o;
        return timestamp.isEqual(that.timestamp) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }
}
