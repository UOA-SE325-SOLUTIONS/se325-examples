package se325.example.bonus02;

/**
 * A simple Person POJO. Nothing special here.
 */
public class Person {

    private long id;
    private String name;
    private String hobby;

    public Person(){}

    public Person(long id, String name, String hobby) {
        this.id = id;
        this.name = name;
        this.hobby = hobby;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", hobby='" + hobby + '\'' +
                '}';
    }
}
