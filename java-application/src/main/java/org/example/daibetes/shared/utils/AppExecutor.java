package org.example.daibetes.shared.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Reusable Central Thread Pool for Asynchronous Background Tasks.
 * Configured with Daemon threads so JVM terminates cleanly when the FX UI is closed.
 */
public class AppExecutor {

    private static final ExecutorService executorInstance = Executors.newFixedThreadPool(4, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true); // Daemon flag guarantees JVM exits cleanly on UI close
            t.setName("dAIbetes-async-worker-" + t.threadId());
            return t;
        }
    });

    public static ExecutorService get() {
        return executorInstance;
    }
}
