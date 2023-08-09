package se325.example.bonus02.proxymagic;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;
import se325.example.bonus02.Person;
import se325.example.bonus02.PersonDatabase;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Creates proxy {@link Person} instances.
 * <p>
 * Unlike {@link java.util.List}, {@link Person} is an object type, not an interface, and Java doesn't ship with the
 * ability to create proxies of object types. Therefore, we are using the ByteBuddy library here. ByteBuddy is also
 * used by Hibernate and several other major libraries to create its proxies and perform other on-the-fly code generation.
 * <p>
 * ByteBuddy is so-named as it creates and manipulates Java ByteCode behind the scenes, then loads its dynamic creations
 * using the Java class loader so your program can run the code that's just been dynamically created.
 *
 * @see <a href="https://bytebuddy.net/#/">ByteBuddy homepage</a>
 */
public class PersonProxyer {

    /**
     * Creates a proxy {@link Person} instance which will lazily load a real person instance when any method is called.
     *
     * @return a proxy {@link Person}
     * @throws ReflectiveOperationException if the voodoo magic doesn't work as expected
     */
    public static Person proxyPerson() throws ReflectiveOperationException {
        Class<? extends Person> proxyPersonClass = new ByteBuddy()
                .subclass(Person.class)
                .method(ElementMatchers.any())
                .intercept(InvocationHandlerAdapter.of(new PersonProxy()))
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded();

        return proxyPersonClass.getConstructor().newInstance();
    }

    /**
     * An invocation handler that will "lazily load" a Person from the "database", and forward all method calls
     * to it.
     */
    private static class PersonProxy implements InvocationHandler {

        private Person realPerson;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            // Lazy loads the person from the "database" the first time a method is called
            if (realPerson == null) {
                realPerson = PersonDatabase.getInstance().readPersonFromDatabase();
            }

            // Forwards the method call onto the real person
            return method.invoke(realPerson, args);
        }
    }
}
