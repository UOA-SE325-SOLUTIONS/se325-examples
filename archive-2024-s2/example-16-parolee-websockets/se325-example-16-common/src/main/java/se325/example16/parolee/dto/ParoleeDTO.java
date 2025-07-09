package se325.example16.parolee.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se325.example16.parolee.common.Gender;
import se325.example16.parolee.jackson.LocalDateDeserializer;
import se325.example16.parolee.jackson.LocalDateSerializer;

import java.time.LocalDate;
import java.util.Objects;

public class ParoleeDTO {

    private Long id;
    private String lastName;
    private String firstName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private AddressDTO homeAddress;
    private MovementDTO lastKnownPosition;

    public ParoleeDTO() {

    }

    public ParoleeDTO(Long id, String lastName, String firstName, Gender gender, LocalDate dateOfBirth, AddressDTO homeAddress) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.homeAddress = homeAddress;
    }

    public ParoleeDTO(String lastName, String firstName, Gender gender, LocalDate dateOfBirth, AddressDTO homeAddress) {
        this(null, lastName, firstName, gender, dateOfBirth, homeAddress);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public AddressDTO getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(AddressDTO homeAddress) {
        this.homeAddress = homeAddress;
    }

    public MovementDTO getLastKnownPosition() {
        return lastKnownPosition;
    }

    public void setLastKnownPosition(MovementDTO lastKnownPosition) {
        this.lastKnownPosition = lastKnownPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParoleeDTO that = (ParoleeDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(lastName, that.lastName)
                && Objects.equals(firstName, that.firstName) && gender == that.gender
                && Objects.equals(dateOfBirth, that.dateOfBirth) && Objects.equals(homeAddress, that.homeAddress)
                && Objects.equals(lastKnownPosition, that.lastKnownPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastName, firstName, gender, dateOfBirth, homeAddress, lastKnownPosition);
    }
}
