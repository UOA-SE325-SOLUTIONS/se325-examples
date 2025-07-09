package se325.example16.parolee.domain;

import se325.example16.parolee.common.Gender;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class to represent a Parolee. A Parolee is described by:
 * - Personal details: lastName, firstName, gender, date-of-birth, home address;
 * - Curfew: any constraints on the Parolee's location;
 * - Criminal profile: criminal history of the Parolee;
 * - Dissassociates: other Parolees who the Parolee is not permitted to be with;
 * - Movements: a timestamped history of where the Parolee has been.
 * <p>
 * A Parolee is uniquely identified by an id value of type Long.
 */
@Entity
public class Parolee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lastName;
    private String firstName;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate dateOfBirth;

    @Embedded
    private Address homeAddress;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Conviction> convictions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Parolee> disassociates = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Movement> movements = new HashSet<>();

    @Embedded
    private Curfew curfew;

    public Parolee() {
    }

    public Parolee(Long id,
                   String lastName,
                   String firstName,
                   Gender gender,
                   LocalDate dateOfBirth,
                   Address homeAddress) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.homeAddress = homeAddress;
    }

    public Parolee(String lastName,
                   String firstName,
                   Gender gender,
                   LocalDate dateOfBirth,
                   Address homeAddress) {
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

    public void addMovement(Movement movement) {
        this.movements.add(movement);
    }

    /**
     * Streams the movements, in a sorted order. Sorted by timestamp, latest first.
     */
    private Stream<Movement> getMovementsStream() {
        return movements.stream()
                .sorted(Comparator.comparing(Movement::getTimestamp, Comparator.reverseOrder()));
    }

    /**
     * Gets the movements, in a sorted order. Sorted by timestamp, latest first.
     */
    public List<Movement> getMovements() {
        return getMovementsStream().collect(Collectors.toUnmodifiableList());
    }

    /**
     * Gets the most recent movement, or null if there are none.
     */
    public Movement getLastKnownPosition() {
        return getMovementsStream().findFirst().orElse(null);
    }

    public Set<Parolee> getDisassociates() {
        return this.disassociates;
    }

    public void setDisassociates(Set<Parolee> disassociates) {
        this.disassociates = disassociates;
    }

    public Set<Conviction> getConvictions() {
        return convictions;
    }

    public void setConvictions(Set<Conviction> convictions) {
        this.convictions = convictions;
    }

    public Curfew getCurfew() {
        return curfew;
    }

    public void setCurfew(Curfew curfew) {
        this.curfew = curfew;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (getClass() != obj.getClass())
            return false;

        Parolee other = (Parolee) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}