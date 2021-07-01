package se325.example07.parolee.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Class to represent a Parolee.
 * <p>
 * A Parolee is simply represented by a unique id, a name, gender and date of birth.
 */
public class Parolee implements Serializable {
    private Long id;
    private String lastName;
    private String firstName;
    private Gender gender;
    private LocalDate dateOfBirth;

    public Parolee() {

    }

    public Parolee(Long id, String firstName, String lastName, Gender gender, LocalDate dateOfBirth) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public Parolee(Long id, String firstName, String lastName, Gender gender, String dateOfBirth) {
        this(id, firstName, lastName, gender, LocalDate.parse(dateOfBirth, DateTimeFormatter.ISO_LOCAL_DATE));
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "Parolee{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", gender=" + gender +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}