package uoa.se325.parolees.dto;

import uoa.se325.parolees.model.Address;
import uoa.se325.parolees.model.Gender;
import uoa.se325.parolees.model.Movement;
import uoa.se325.parolees.model.Parolee;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Consumer;

public class ParoleeDTO {

    private Long id;
    private String lastName;
    private String firstName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private Address homeAddress;

    private Movement lastKnownPosition;

    public ParoleeDTO() {

    }

    public ParoleeDTO(String lastName, String firstName, Gender gender, LocalDate dateOfBirth, Address homeAddress) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.homeAddress = homeAddress;
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

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Movement getLastKnownPosition() {
        return lastKnownPosition;
    }

    public void setLastKnownPosition(Movement lastKnownPosition) {
        this.lastKnownPosition = lastKnownPosition;
    }

    @Override
    public String toString() {
        return "ParoleeDTO{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", gender=" + gender +
                ", dateOfBirth=" + dateOfBirth +
                ", homeAddress=" + homeAddress +
                ", lastKnownPosition=" + lastKnownPosition +
                '}';
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

    public static ParoleeDTO fromDomain(Parolee domainParolee) {
        ParoleeDTO dtoParolee = new ParoleeDTO();
        dtoParolee.setId(domainParolee.getId());
        dtoParolee.setDateOfBirth(domainParolee.getDateOfBirth());
        dtoParolee.setGender(domainParolee.getGender());
        dtoParolee.setFirstName(domainParolee.getFirstName());
        dtoParolee.setLastName(domainParolee.getLastName());
        dtoParolee.setHomeAddress(domainParolee.getHomeAddress());

        dtoParolee.setLastKnownPosition(domainParolee.getLastKnownPosition());

        return dtoParolee;
    }

    public Parolee toDomain() {
        Parolee domainParolee = new Parolee();
        updateDomain(domainParolee);
        return domainParolee;
    }

    public void updateDomain(Parolee domainParolee) {
        updateField(id, domainParolee::setId);
        updateField(dateOfBirth, domainParolee::setDateOfBirth);
        updateField(gender, domainParolee::setGender);
        updateField(firstName, domainParolee::setFirstName);
        updateField(lastName, domainParolee::setLastName);
        updateField(homeAddress, domainParolee::setHomeAddress);
    }

    private <T> void updateField(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
