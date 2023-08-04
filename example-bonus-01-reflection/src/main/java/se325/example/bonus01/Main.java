package se325.example.bonus01;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException, ReflectiveOperationException {

        Map<String, Object> methodArgs = new HashMap<>();
        methodArgs.put("name", "Alice");
        methodArgs.put("v1", 4);
        methodArgs.put("v2", 3);

        ReflectiveMethodInvoker.invokeAll("se325.example.bonus01", methodArgs);
    }

}
