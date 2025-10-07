package io.github.barhan44.api;

import io.github.barhan44.model.DetectionResult;

/**
 * Core interface for all encoding detectors.
 * <p>
 * Implementations of this interface analyze byte arrays to determine the character encoding.
 * Detectors may support one or multiple encodings and provide confidence levels for their detection.
 * </p>
 * <p>
 * Typical usage:
 * <pre>
 * EncodingDetector detector = ...;
 * DetectionResult result = detector.detect(data);
 * </pre>
 */
public interface EncodingDetector {

    /**
     * Attempts to detect the encoding of the given byte array data.
     *
     * @param data the byte array to analyze
     * @return an DetectionResult describing the detected encoding and confidence,
     * or null if detection failed
     */
    DetectionResult detect(byte[] data);

    /**
     * Returns the human-readable name of the detector.
     *
     * @return detector name
     */
    String getName();

    /**
     * Checks if the detector can analyze the provided byte array data.
     *
     * @param data the byte array to check
     * @return true if this detector can attempt detection on the data, false otherwise
     */
    boolean canDetect(byte[] data);

    /**
     * Returns the list of encodings supported by this detector.
     *
     * @return array of supported encoding names (e.g., "UTF-8", "Windows-1251")
     */
    String[] getSupportedEncodings();
}
