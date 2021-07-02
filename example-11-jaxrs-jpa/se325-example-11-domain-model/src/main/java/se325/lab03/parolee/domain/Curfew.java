package se325.lab03.parolee.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se325.lab03.parolee.jackson.LocalTimeDeserializer;
import se325.lab03.parolee.jackson.LocalTimeSerializer;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Class to represent a Parolee's curfew. A curfew is described by a confinement
 * address and the period of time that the curfew is in effect (typically
 * overnight).
 */
public class Curfew {

    private Address confinementAddress;

    private LocalTime startTime;

    private LocalTime endTime;

    protected Curfew() {
    }

    public Curfew(Address confinementAddress,
                  LocalTime startTime,
                  LocalTime endTime) {
        this.confinementAddress = confinementAddress;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Address getConfinementAddress() {
        return confinementAddress;
    }

    public void setConfinementAddress(Address confinementAddress) {
        this.confinementAddress = confinementAddress;
    }

    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Curfew curfew = (Curfew) o;
        return Objects.equals(confinementAddress, curfew.confinementAddress) && Objects.equals(startTime, curfew.startTime) && Objects.equals(endTime, curfew.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(confinementAddress, startTime, endTime);
    }
}
