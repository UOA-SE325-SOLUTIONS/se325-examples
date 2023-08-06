package se325.example.bonus01;

import se325.example.bonus01.annotations.MethodParam;
import se325.example.bonus01.annotations.RunnableClass;
import se325.example.bonus01.annotations.RunnableMethod;

/**
 * A test class which has been annotated. The {@link ReflectiveMethodInvoker} will be able to discover this class,
 * read its annotations, and decide what to do with it (i.e. invoke the annotated methods, supplied with the annotated
 * method arguments) based on those annotations.
 */
@RunnableClass
public class MyRunnableClass {

    @RunnableMethod
    public void greet(@MethodParam("name") String name) {
        System.out.println("Hello, " + name + "!");
    }

    @RunnableMethod
    public void multiply(@MethodParam("v1") int first, @MethodParam("v2") int second) {
        System.out.println(first + " x " + second + " = " + (first * second));
    }

    @RunnableMethod
    public void helloWorld() {
        System.out.println("Hello, world!");
    }

    public void notRunnable() {
        System.err.println("You should never see this message.");
    }

}
