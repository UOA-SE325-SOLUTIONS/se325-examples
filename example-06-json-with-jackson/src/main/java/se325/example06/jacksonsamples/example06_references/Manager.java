package se325.example06.jacksonsamples.example06_references;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Manager extends Employee {

    private List<Employee> employees = new ArrayList<>();

    public Manager() {
    }

    public Manager(int id, String name) {
        super(id, name);
    }

    public Manager(int id, String name, Employee... employees) {
        this(id, name);
        this.employees.addAll(Arrays.asList(employees));
        for (Employee e : employees) {
            e.setManager(this);
        }
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}
