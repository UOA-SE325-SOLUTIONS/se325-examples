package se325.example13.parolee.domain;

import se325.example13.parolee.common.Offence;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Class to represent a particular criminal conviction. A conviction is made up
 * of one or more CriminalProfile.Offence tags, the date of conviction, and a
 * description of the conviction. Convictions are immutable.
 */
@Embeddable
@Access(AccessType.FIELD)
public class Conviction {

    @Enumerated(EnumType.STRING)
    private Offence offence;

    private LocalDate date;

    private String description;

    public Conviction() {
    }

    public Conviction(LocalDate convictionDate, String description, Offence offence) {
        date = convictionDate;
        this.description = description;
        this.offence = offence;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Offence getOffence() {
        return offence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conviction that = (Conviction) o;
        return Objects.equals(offence, that.offence) && date.isEqual(that.date) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offence, date, description);
    }
}
