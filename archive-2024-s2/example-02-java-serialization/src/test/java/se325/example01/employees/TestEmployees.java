package se325.example01.employees;

import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class TestEmployees {

    private Manager mgr;
    private Employee e1;
    private Employee e2;

    @Before
    public void setUp() {
        mgr = new Manager("David", "8653899");
        e1 = new Employee("Tim", "2368571", mgr);
        e2 = new Employee("Gareth", "0911558", mgr);

        assertEquals("8653899", mgr.getSsn());
        // etc...
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        try (ObjectOutputStream objOut = new ObjectOutputStream(bytesOut)) {
            objOut.writeObject(mgr);
        }

        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytesOut.toByteArray());
        try (ObjectInputStream objIn = new ObjectInputStream(bytesIn)) {

            Manager deserialized = (Manager) objIn.readObject();

            // They aren't the same object...
            assertNotSame(mgr, deserialized);

            // But they have the same contents.
            assertEquals("David", deserialized.getName());
            assertEquals("8653899", deserialized.getSsn());

            // Same with referenced objects.
            assertNotSame(e1, deserialized.getResponsibleFor().get(0));
            assertNotSame(e2, deserialized.getResponsibleFor().get(1));
            assertEquals(e1, deserialized.getResponsibleFor().get(0));
            assertEquals(e2, deserialized.getResponsibleFor().get(1));

        }

    }

}
