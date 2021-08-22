package se325.example16.parolee.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Information about a parole violation which has occurred.
 */
public class ParoleViolationDTO {

    private long paroleeId;
    private GeoPositionDTO location;

    @JsonCreator
    public ParoleViolationDTO(@JsonProperty("paroleeId") long paroleeId,
                              @JsonProperty("location") GeoPositionDTO location) {
        this.paroleeId = paroleeId;
        this.location = location;
    }

    public long getParoleeId() {
        return paroleeId;
    }

    public GeoPositionDTO getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParoleViolationDTO that = (ParoleViolationDTO) o;
        return paroleeId == that.paroleeId && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paroleeId);
    }
}
