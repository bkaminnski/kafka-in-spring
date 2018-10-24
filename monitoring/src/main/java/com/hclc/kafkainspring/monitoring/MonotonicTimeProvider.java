package com.hclc.kafkainspring.monitoring;

/**
 * You don't need this class in production. I created it just to have some reasonable place to make notes regarding System.nanoTime() vs System.currentTimeMillis().
 */
public class MonotonicTimeProvider {

    private MonotonicTimeProvider() {
        throw new AssertionError();
    }

    /**
     * Designing Data-Intensive Applications by Martin Kleppmann, p. 288, Monotonic Versus Time-of-Day Clocks:
     * <p>
     * System.currentTimeMillis() can travel back and forth in time.
     * <p>
     * System.nanoTime() is meaningless in terms of wall-clock time,
     * but is guaranteed to be monotonic within a single instance of JVM and can be safely used to measure elapsed time.
     *
     * @return mono
     */
    public static long monotonicNowInNano() {
        return System.nanoTime();
    }
}
