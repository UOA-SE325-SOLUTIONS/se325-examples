package se325.example01.employees;

import java.io.Serializable;

public class EmployeeRequest implements Serializable {

    private String name;

    public EmployeeRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
