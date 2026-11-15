package io.github.barhan44.factory;

import io.github.barhan44.api.EncodingDetector;
import io.github.barhan44.impl.UTF8EncodingDetector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Concurrency tests for the {@link EncodingDetectorFactory} singleton.
 */
class EncodingDetectorFactoryConcurrencyTest {

    private static final int THREADS = 8;

    @Test
    void concurrentCreateDetectorIsSafe() throws Exception {
        EncodingDetectorFactory factory = EncodingDetectorFactory.getInstance();
        int iterations = 100;

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            futures.add(pool.submit(() -> {
                start.await();
                for (int j = 0; j < iterations; j++) {
                    EncodingDetector utf8 = factory.createDetector("UTF-8");
                    EncodingDetector cp1251 = factory.createDetector("Windows-1251");
                    if (utf8 == null || cp1251 == null) {
                        return false;
                    }
                }
                return true;
            }));
        }

        start.countDown();

        try {
            for (Future<Boolean> future : futures) {
                assertTrue(future.get(10, TimeUnit.SECONDS));
            }
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void concurrentRegistrationRetainsAllRegisteredDetectors() throws Exception {
        EncodingDetectorFactory factory = EncodingDetectorFactory.getInstance();
        int perThread = 1000;

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();
        Set<String> expectedNames = new HashSet<>();

        for (int thread = 0; thread < THREADS; thread++) {
            final int threadIndex = thread;
            for (int i = 0; i < perThread; i++) {
                expectedNames.add("ConcurrentDetector-" + threadIndex + "-" + i);
            }
            futures.add(pool.submit(() -> {
                start.await();
                for (int i = 0; i < perThread; i++) {
                    factory.registerDetector(
                            "ConcurrentDetector-" + threadIndex + "-" + i,
                            UTF8EncodingDetector.class);
                }
                return null;
            }));
        }

        start.countDown();

        try {
            for (Future<?> future : futures) {
                future.get(10, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }

        Set<String> registered = factory.getAvailableDetectors();
        int missing = 0;
        for (String name : expectedNames) {
            if (!registered.contains(name)) {
                missing++;
            }
        }

        assertTrue(missing == 0, "lost " + missing + " of " + expectedNames.size()
                + " concurrent registrations");
    }
}
