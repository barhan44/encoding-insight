package io.github.barhan44.base;

import io.github.barhan44.api.EncodingDetector;
import io.github.barhan44.model.DetectionResult;

/**
 * Abstract base class for encoding detectors.
 * <p>
 * Implements the common detection workflow and provides template method {@code performDetection(byte[] data)}
 * that subclasses must override to provide custom detection logic.
 * </p>
 * <p>
 * This class handles basic validation and delegates actual detection to subclasses,
 * enforcing a consistent detection interface.
 */
public abstract class AbstractEncodingDetector implements EncodingDetector {
    protected final String name;
    protected final String[] supportedEncodings;

    /**
     * Constructs an AbstractEncodingDetector.
     *
     * @param name               human-readable detector name
     * @param supportedEncodings names of supported encodings by this detector
     */
    protected AbstractEncodingDetector(String name, String[] supportedEncodings) {
        this.name = name;
        this.supportedEncodings = supportedEncodings;
    }

    /**
     * Performs the actual encoding detection on the given byte array.
     * Subclasses must implement this method with their specific detection algorithm.
     *
     * @param data the byte array to analyze
     * @return the DetectionResult with detected encoding and confidence,
     * or null if detection failed
     */
    protected abstract DetectionResult performDetection(byte[] data);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getSupportedEncodings() {
        return supportedEncodings.clone();
    }

    @Override
    public DetectionResult detect(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        if (!canDetect(data)) {
            return null;
        }

        return performDetection(data);
    }
}
