package io.github.barhan44.encoding.insight.configuration;

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


    /**
     * Indicates whether strict detection mode is enabled.
     * <p/>
     * When strict detection is enabled, the detector performs more rigorous checks
     * to ensure accurate encoding detection, which may result in longer processing times.
     *
     * @return true if strict detection is enabled, false otherwise
     */
    boolean isStrictDetection();

    /**
     * Returns the maximum number of bytes to read from the file for encoding detection.
     * <p/>
     * This limit helps optimize performance by restricting the amount of data analyzed.
     * The detector will only consider the specified number of bytes from the beginning of the file.
     *
     * @return maximum number of bytes read
     */
    int getMaxReadBytes();
}
