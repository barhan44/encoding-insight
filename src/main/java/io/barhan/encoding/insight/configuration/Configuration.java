package io.barhan.encoding.insight.configuration;

/**
 * The {@code Configuration} interface defines the configuration settings
 * required for encoding detection operations
 */
public interface Configuration {
    /**
     * Returns the buffer size used during encoding detection operations.
     *
     * @return the buffer size in bytes
     */
    int getBufferSize();
}
