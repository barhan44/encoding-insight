package io.github.barhan44.model;

import java.nio.charset.Charset;

/**
 * Represents the result of a character encoding detection attempt.
 * <p>
 * Encapsulates the name of the detected encoding, a confidence score, the name of
 * the detector that produced the result and, when available, the corresponding
 * {@link Charset}. The string-based {@link #getEncoding()} accessor is retained for
 * backward compatibility; new code can use {@link #getCharset()} to obtain a
 * ready-to-use charset object.
 * </p>
 * <p>
 * Confidence values range from 0 to 100, representing the certainty of detection.
 * </p>
 */
public class DetectionResult {
    private final String encoding;
    private final int confidence;
    private final String detectorName;
    private final Charset charset;

    /**
     * Creates a new detection result and resolves the charset from the encoding name.
     * If the name is not recognized by the JVM, {@link #getCharset()} returns
     * {@code null}.
     *
     * @param encoding     the name of the detected encoding (e.g., "UTF-8", "Windows-1251")
     * @param confidence   an integer confidence level in the range 0–100
     * @param detectorName the name of the detector that produced this result
     */
    public DetectionResult(String encoding, int confidence, String detectorName) {
        this(encoding, confidence, detectorName, resolveCharset(encoding));
    }

    /**
     * Creates a new detection result with an explicit charset.
     *
     * @param encoding     the name of the detected encoding
     * @param confidence   an integer confidence level in the range 0–100
     * @param detectorName the name of the detector that produced this result
     * @param charset      the resolved charset, or {@code null} if unavailable
     */
    public DetectionResult(String encoding, int confidence, String detectorName, Charset charset) {
        this.encoding = encoding;
        this.confidence = confidence;
        this.detectorName = detectorName;
        this.charset = charset;
    }

    /**
     * Returns the detected encoding name.
     *
     * @return encoding name as a String
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Returns the detected charset, resolved from the encoding name or supplied
     * explicitly.
     *
     * @return the detected {@link Charset}, or {@code null} if the encoding name is
     * not recognized by the JVM
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Returns the confidence level of the detection.
     *
     * @return confidence as an integer percentage (0–100)
     */
    public int getConfidence() {
        return confidence;
    }

    /**
     * Returns the name of the detector that produced this result.
     *
     * @return detector name as a String
     */
    public String getDetectorName() {
        return detectorName;
    }

    @Override
    public String toString() {
        return "DetectionResult{" +
                "encoding='" + encoding + '\'' +
                ", confidence=" + confidence +
                ", detectorName='" + detectorName + '\'' +
                '}';
    }

    private static Charset resolveCharset(String encoding) {
        if (encoding == null) {
            return null;
        }
        try {
            return Charset.forName(encoding);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
