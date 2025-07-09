package se325.example.bonus02;

import se325.example.bonus02.annotations.LazyLoaded;
import se325.example.bonus02.annotations.WrappedList;

import java.util.ArrayList;
import java.util.List;

/**
 * We will use reflection to wrap {@link #demoList} in another list which logs calls to add() and remove(). We will also
 * use it to add a proxy {@link Person} for {@link #dave}, which will be populated properly upon first property access.
 */
public class AnnotatedDemoClass {

    @WrappedList
    private List<String> demoList = new ArrayList<>();

    @LazyLoaded
    private Person dave;


    public List<String> getDemoList() {
        return demoList;
    }

    public Person getDave() {
        return dave;
    }
}
