package se325.example.bonus02;

/**
 * A pretend factory for creating person objects. Will be used by the person proxy to "lazy load a real person" when
 * required.
 */
public class PersonDatabase {

    private PersonDatabase() {
    }

    private static PersonDatabase instance;

    public static PersonDatabase getInstance() {
        if (instance == null) instance = new PersonDatabase();
        return instance;
    }

    public Person readPersonFromDatabase() {
        return new Person(1, "Dave", "Stuff & Things");
    }
}
