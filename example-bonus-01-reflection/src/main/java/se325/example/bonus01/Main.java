package se325.example.bonus01;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    /**
     * Main program entry points. Passes some args into the {@link ReflectiveMethodInvoker}, which act as values to
     * be passed into various invoked methods.
     *
     * @param args
     * @throws IOException                  if there's an error reading information from the class loader.
     * @throws ReflectiveOperationException if any reflective code malfunctions in some way (class not found, method has
     *                                      wrong parameter types, method throws an exception, etc).
     */
    public static void main(String[] args) throws IOException, ReflectiveOperationException {

        Map<String, Object> methodArgs = new HashMap<>();
        methodArgs.put("name", "Alice");
        methodArgs.put("v1", 4);
        methodArgs.put("v2", 3);

        ReflectiveMethodInvoker.invokeAll("se325.example.bonus01", methodArgs);
    }

}
