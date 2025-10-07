package io.github.barhan44.model;

/**
 * Represents the result of a character encoding detection attempt.
 * <p>
 * Encapsulates the name of the detected encoding, a confidence score,
 * and the name of the detector that produced the result.
 * </p>
 * <p>
 * Confidence values range from 0 to 100, representing the certainty of detection.
 * </p>
 */
public class DetectionResult {
    private final String encoding;
    private final int confidence;
    private final String detectorName;

    /**
     * Constructs an EncodingDetectionResult.
     *
     * @param encoding     the name of the detected encoding (e.g., "UTF-8", "Windows-1251")
     * @param confidence   an integer confidence level in the range 0–100
     * @param detectorName the name of the detector that produced this result
     */
    public DetectionResult(String encoding, int confidence, String detectorName) {
        this.encoding = encoding;
        this.confidence = confidence;
        this.detectorName = detectorName;
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
}
