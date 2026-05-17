package org.example.daibetes.shared.utils;

import org.example.daibetes.core.database.CalendarDAO;
import org.example.daibetes.core.database.ICalendarDAO;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * A Thread-Safe Singleton Service Registry for Dependency Injection.
 */
public class ServiceRegistry {
    
    private static final ServiceRegistry INSTANCE = new ServiceRegistry();
    private final Map<Class<?>, Object> container = new ConcurrentHashMap<>();

    static {
        // Statically self-register default implementations.
        // This guarantees that dependencies are always registered regardless of which
        // entry point (Launcher.java, splashApplication.java, or HelloApplication.java) is used.
        INSTANCE.register(ICalendarDAO.class, new CalendarDAO());
    }

    private ServiceRegistry() {}

    public static ServiceRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a service implementation under a specified interface key.
     */
    public <T> void register(Class<T> serviceInterface, T implementation) {
        if (implementation == null) {
            throw new IllegalArgumentException("Service implementation cannot be null.");
        }
        container.put(serviceInterface, implementation);
    }

    /**
     * Resolves and returns the registered implementation for an interface.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> serviceInterface) {
        T instance = (T) container.get(serviceInterface);
        if (instance == null) {
            throw new IllegalStateException("No service implementation registered for: " + serviceInterface.getName());
        }
        return instance;
    }
}
