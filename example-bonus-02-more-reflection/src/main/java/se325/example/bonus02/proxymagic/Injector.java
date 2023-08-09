package se325.example.bonus02.proxymagic;

import se325.example.bonus02.annotations.LazyLoaded;
import se325.example.bonus02.annotations.WrappedList;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Uses reflection on annotations present in a provided object to inject various proxies into that object.
 */
public class Injector {

    /**
     * Injects proxies into the given object. Loops through each field in that object, injecting if required.
     */
    public static void inject(Object obj) throws ReflectiveOperationException {

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (var field : fields) {
            inject(obj, field);
        }
    }

    /**
     * Injects proxies into the given field of the given object, if annotated appropriately. If annotated with
     * {@link LazyLoaded}, we will inject a proxy {@link se325.example.bonus02.Person} class which will "lazy load" a
     * "real" person when a method is called. If annotated with {@link WrappedList}, we will instead inject a proxy
     * {@link List} implementation, wrapping the list that's already there, which will log calls.
     *
     * @param obj   the object to inject
     * @param field the field of the object to inject
     * @throws ReflectiveOperationException if something goes wrong with injection
     */
    private static void inject(Object obj, Field field) throws ReflectiveOperationException {
        if (field.isAnnotationPresent(LazyLoaded.class)) {
            injectProxyPerson(obj, field);
        }
        if (field.isAnnotationPresent(WrappedList.class)) {
            injectWrappedList(obj, field);
        }
    }

    private static void injectProxyPerson(Object obj, Field field) throws ReflectiveOperationException {
        field.setAccessible(true);
        field.set(obj, PersonProxyer.proxyPerson());
    }

    private static void injectWrappedList(Object obj, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        List<?> existingList = (List<?>) field.get(obj);
        field.set(obj, ListWrapper.wrap(existingList));
    }
}
