package io.github.barhan44.encoding.insight.core.optimization;

/**
 * The {@code OptimizationOptions} interface defines optimization settings
 * for encoding detection operations
 */
public interface OptimizationOptions {
    /**
     * Returns whether caching is enabled for detection results.
     *
     * @return {@code true} if caching is enabled; {@code false} otherwise
     */
    boolean isCachingEnabled();
}
