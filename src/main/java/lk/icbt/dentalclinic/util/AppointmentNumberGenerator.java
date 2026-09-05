package lk.icbt.dentalclinic.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton (Task B design pattern requirement).
 * Guarantees exactly one counter is shared across the whole running application,
 * so two receptionists registering appointments at the same time never collide
 * on the same appointment number.
 */
public final class AppointmentNumberGenerator {

    private static volatile AppointmentNumberGenerator instance;
    private final AtomicInteger counter = new AtomicInteger(1000);

    private AppointmentNumberGenerator() {
    }

    public static AppointmentNumberGenerator getInstance() {
        if (instance == null) {
            synchronized (AppointmentNumberGenerator.class) {
                if (instance == null) {
                    instance = new AppointmentNumberGenerator();
                }
            }
        }
        return instance;
    }

    /** Thread-safe next appointment number, e.g. A1001, A1002, ... */
    public synchronized String next() {
        return "A" + counter.incrementAndGet();
    }
}
