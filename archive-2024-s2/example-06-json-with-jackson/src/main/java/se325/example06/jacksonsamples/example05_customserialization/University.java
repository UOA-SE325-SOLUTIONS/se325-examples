package se325.example06.jacksonsamples.example05_customserialization;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class University {

    @JsonDeserialize(keyUsing = CourseDeserializer.class)
    private Map<Course, List<Student>> enrollments = new TreeMap<>();

    public Map<Course, List<Student>> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(Map<Course, List<Student>> enrollments) {
        this.enrollments = enrollments;
    }
}
