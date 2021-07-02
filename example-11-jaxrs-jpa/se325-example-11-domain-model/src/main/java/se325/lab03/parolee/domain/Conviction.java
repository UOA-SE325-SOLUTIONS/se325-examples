package se325.lab03.parolee.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se325.lab03.parolee.jackson.LocalDateDeserializer;
import se325.lab03.parolee.jackson.LocalDateSerializer;

import java.time.LocalDate;
import java.util.*;

/**
 * Class to represent a particular criminal conviction. A conviction is made up
 * of one or more CriminalProfile.Offence tags, the date of conviction, and a
 * description of the conviction. Convictions are immutable.
 */
public class Conviction {

    private Set<Offence> offenceTags;

    private LocalDate date;

    private String description;

    public Conviction() {
        this(null, null);
    }

    @JsonCreator
    public Conviction(@JsonProperty("date") LocalDate convictionDate,
                      @JsonProperty("description") String description,
                      @JsonProperty("offenceTags") Offence... offenceTags) {
        date = convictionDate;
        this.description = description;
        this.offenceTags = new HashSet<>(Arrays.asList(offenceTags));
    }

    public Set<Offence> getOffenceTags() {
        return Collections.unmodifiableSet(offenceTags);
    }

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conviction that = (Conviction) o;
        return Objects.equals(offenceTags, that.offenceTags) && Objects.equals(date, that.date) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offenceTags, date, description);
    }
}
