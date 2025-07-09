package se325.example.bonus01;

import se325.example.bonus01.annotations.RunnableClass;
import se325.example.bonus01.annotations.RunnableMethod;
import se325.example.bonus01.annotations.MethodParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A very basic reflective method invoker class which scans for classes in a given package with {@link RunnableClass}
 * annotations, instantiates them, and calls their {@link RunnableMethod} methods, with their parameters populated by
 * the given args, according to {@link MethodParam} annotations.
 * <p>
 * This is intended as an example of how reflective middleware such as JAX-RS functions behind the scenes.
 */
public class ReflectiveMethodInvoker {

    /**
     * Calls {@link #invokeClass(Class, Map)} on all classes annotated with {@link RunnableClass} in the given package.
     *
     * @param packageName the package
     * @param args        the arguments to pass to {@link #invokeClass(Class, Map)}
     * @throws IOException                  if we can't read class info
     * @throws ReflectiveOperationException if there's an error scanning for classes, instantiating classes, or calling methods.
     */
    public static void invokeAll(String packageName, Map<String, Object> args) throws IOException, ReflectiveOperationException {

        var classes = getAnnotatedClasses(RunnableClass.class, packageName);
        System.out.println(classes.size() + " class(es) found annotated with @RunnableClass");
        for (var clazz : classes) {
            invokeClass(clazz, args);
        }
    }

    /**
     * Creates an instance of the given class, then calls {@link #invokeMethod(Object, Method, Map)} on all methods in that
     * class annotated with {@link RunnableMethod}.
     * <p>
     * Assumes that the given class is non-abstract and has a public no-argument constructor.
     *
     * @param clazz the class to scan
     * @param args  the arguments to pass to {@link #invokeMethod(Object, Method, Map)}
     * @throws ReflectiveOperationException if there's an error instantiating the class or calling its methods.
     */
    public static void invokeClass(Class<?> clazz, Map<String, Object> args) throws ReflectiveOperationException {

        System.out.println("Creating instance of class " + clazz.getName());
        Object instance = clazz.getConstructor().newInstance();

        var methods = Arrays.stream(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(RunnableMethod.class))
                .collect(Collectors.toList());

        for (var method : methods) {
            invokeMethod(instance, method, args);
        }
    }

    /**
     * Invokes the given method on the given object with the given named args. Args are mapped to actual method params
     * based on annotations ({@link MethodParam}). If some method parameters are not annotated, they will be supplied
     * with the default value for that parameter type (<code>null</code> for object types, 0 for integers, etc).
     *
     * @param object the object on which to invoke the method
     * @param method the method to invoke
     * @param args   args which should be supplied to annotated method params.
     * @throws ReflectiveOperationException
     */
    public static void invokeMethod(Object object, Method method, Map<String, Object> args) throws ReflectiveOperationException {

        var parameterValues = getParameters(method, args);
        System.out.println("Invoking " + method.getName());
        method.invoke(object, parameterValues);

    }

    /**
     * Creates an array of parameters to pass to a method to invoke.
     * <p>
     * For each of that method's parameters, if it has the {@link MethodParam} annotation, we will use the value of the
     * <code>args</code> array whose key equals that annotation's <code>value</code>. Otherwise, we will use the default
     * value for that parameter (<code>null</code> for object types, <code>0</code> for ints, etc).
     *
     * @param method
     * @param args
     * @return
     */
    private static Object[] getParameters(Method method, Map<String, Object> args) {
        var parameterTypes = method.getParameterTypes();
        var parameterAnnotations = method.getParameterAnnotations();
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = getParameterValue(parameterTypes[i], parameterAnnotations[i], args);
        }

        return parameters;
    }

    /**
     * Gets the value which should be supplied to a method, given a particular parameter type, and the annotations on
     * that parameter. If annotated with the {@link MethodParam} annotation, an associated value in the given args map
     * should be used, if available. Otherwise, the default value for that parameter type should be used.
     *
     * @param parameterType        the parameter's type
     * @param parameterAnnotations any annotations on the parameter
     * @param args                 the map where the parameter values should be obtained, if available.
     * @return the value to be supplied to the method for that parameter
     */
    private static Object getParameterValue(Class<?> parameterType, Annotation[] parameterAnnotations, Map<String, Object> args) {
        var opt = Arrays.stream(parameterAnnotations)
                .filter(a -> a.annotationType() == MethodParam.class)
                .findFirst();

        if (opt.isPresent()) {
            var annotation = (MethodParam) (opt.get());
            if (args.containsKey(annotation.value())) {
                return args.get(annotation.value());
            }
            return getDefaultValue(parameterType);
        }

        return getDefaultValue(parameterType);
    }

    /**
     * Gets the default value of a given class. This will be null for object types, 0 for ints, false for booleans, etc.
     * This works by creating a one-element array of the given type. Java will automatically fill an array with the
     * default values.
     *
     * @param clazz the class whose default value we return
     * @return the default value of that type
     */
    private static Object getDefaultValue(Class<?> clazz) {
        return Array.get(Array.newInstance(clazz, 1), 0);
    }

    /**
     * Uses the Java system class loader to get all classes in a given package with a given marker annotation.
     *
     * @param annotationClass the annotation to look for on the classes
     * @param packageName     the package to search
     * @param <T>             the type of annotation
     * @return a list of all classes in the given package marked with the given annotation
     * @throws IOException if there's an error reading from the class loader
     */
    private static <T extends Annotation> Set<Class<?>> getAnnotatedClasses(Class<T> annotationClass, String packageName) throws IOException {
        var packageDirName = packageName.replaceAll("[.]", "/");
        try (var in = ClassLoader.getSystemClassLoader().getResourceAsStream(packageDirName)) {
            try (var reader = new BufferedReader(new InputStreamReader(in))) {
                return reader.lines()
                        .filter(line -> line.endsWith(".class"))
                        .map(line -> getClass(line, packageName))
                        .filter(Objects::nonNull)
                        .filter(clazz -> clazz.isAnnotationPresent(annotationClass))
                        .collect(Collectors.toSet());
            }
        }
    }

    /**
     * Gets the class with the given name in the given package.
     *
     * @param className   the class name, including ".class"
     * @param packageName the package name
     * @return the {@link Class}
     */
    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
            return null;
        }
    }
}
