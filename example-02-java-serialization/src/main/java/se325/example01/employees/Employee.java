package se325.example01.employees;

import java.io.Serializable;
import java.util.Objects;

public class Employee implements Serializable {

    protected String name;
    protected String ssn;
    protected Manager manager;

    public Employee(String name, String ssn, Manager manager) {
        this.name = name;
        this.ssn = ssn;
        this.manager = manager;

        if (manager != null) {
            manager.addEmployee(this);
        }
    }

    public Employee(String name, String ssn) {
        this(name, ssn, null);
    }

    public String getName() {
        return name;
    }

    public String getSsn() {
        return ssn;
    }

    public Manager getManager() {
        return manager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return name.equals(employee.name) &&
                ssn.equals(employee.ssn) &&
                Objects.equals(manager, employee.manager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ssn, manager);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", ssn='" + ssn + '\'' +
                ", manager=" + (manager == null ? "none" : "'" + manager.getName() + "'") +
                '}';
    }
}
