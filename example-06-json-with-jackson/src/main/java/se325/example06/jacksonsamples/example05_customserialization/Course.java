package se325.example06.jacksonsamples.example05_customserialization;

import java.util.Objects;

public class Course implements Comparable<Course> {

    private String name;

    public Course(String name) {
        this.name = name;
    }

    public Course() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(name, course.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(Course o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
