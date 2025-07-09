package se325.example16.parolee.domain;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class Curfew {

    @Column(name = "CURFEW_START", nullable = true)
    private LocalTime startTime;

    @Column(name = "CURFEW_END", nullable = true)
    private LocalTime endTime;

    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "CURFEW_LAT", nullable = true)),
            @AttributeOverride(name = "longitude", column = @Column(name = "CURFEW_LNG", nullable = true))
    })
    private GeoPosition confinementLocation;

    @Column(name = "CURFEW_RADIUS", nullable = true)
    private Integer confinementRadius;

    public Curfew(){}

    public Curfew(LocalTime startTime, LocalTime endTime, GeoPosition confinementLocation, Integer confinementRadius) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.confinementLocation = confinementLocation;
        this.confinementRadius = confinementRadius;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public GeoPosition getConfinementLocation() {
        return confinementLocation;
    }

    public void setConfinementLocation(GeoPosition confinementLocation) {
        this.confinementLocation = confinementLocation;
    }

    public Integer getConfinementRadius() {
        return confinementRadius;
    }

    public void setConfinementRadius(Integer confinementRadius) {
        this.confinementRadius = confinementRadius;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Curfew curfew = (Curfew) o;
        return confinementRadius == curfew.confinementRadius &&
                Objects.equals(startTime, curfew.startTime) &&
                Objects.equals(endTime, curfew.endTime) &&
                Objects.equals(confinementLocation, curfew.confinementLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, confinementRadius);
    }
}
