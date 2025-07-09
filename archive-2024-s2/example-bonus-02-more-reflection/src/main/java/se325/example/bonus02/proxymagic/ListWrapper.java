package se325.example.bonus02.proxymagic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Creates proxy {@link List} implementations which wrap given existing {@link List}s. All method calls are forwarded
 * to the provided {@link List}s, but are also logged to the console.
 * <p>
 * This class achieves its functionality through Java's built-in {@link Proxy} class, part of the Reflection library. It
 * can create proxies of any interface type, which we supply with an {@link InvocationHandler} implementation to actually
 * handle the method calls.
 */
public class ListWrapper {

    public static <T> List<T> wrap(List<T> list) {

        return (List<T>) Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                new Class[]{List.class},
                new LoggerListInvocationHandler<T>(list));

    }

    private static class LoggerListInvocationHandler<T> implements InvocationHandler {

        private final List<T> wrappedList;

        public LoggerListInvocationHandler(List<T> wrappedList) {
            this.wrappedList = wrappedList;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("LOG: List method called: " + method.getName() + "()");
            return method.invoke(wrappedList, args);
        }
    }
}
