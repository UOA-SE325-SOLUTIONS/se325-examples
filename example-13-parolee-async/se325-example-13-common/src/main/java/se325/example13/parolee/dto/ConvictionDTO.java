package se325.example13.parolee.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se325.example13.parolee.common.Offence;
import se325.example13.parolee.jackson.LocalDateDeserializer;
import se325.example13.parolee.jackson.LocalDateSerializer;

import java.time.LocalDate;
import java.util.Objects;

public class ConvictionDTO {

    private final Offence offence;
    private final LocalDate date;
    private final String description;

    @JsonCreator
    public ConvictionDTO(@JsonProperty("date") LocalDate convictionDate,
                         @JsonProperty("description") String description,
                         @JsonProperty("offence") Offence offence) {
        date = convictionDate;
        this.description = description;
        this.offence = offence;
    }

    public Offence getOffence() {
        return offence;
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
        ConvictionDTO that = (ConvictionDTO) o;
        return offence == that.offence && date.isEqual(that.date) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offence, date, description);
    }
}
