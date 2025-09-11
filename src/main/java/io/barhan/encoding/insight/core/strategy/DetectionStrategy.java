package io.barhan.encoding.insight.core.strategy;

import io.barhan.encoding.insight.core.api.DetectionResult;

/**
 * The {@code DetectionStrategy} interface defines the contract for encoding
 * detection strategies
 */
public interface DetectionStrategy {
    /**
     * Detects the character encoding of the provided byte data
     *
     * @param data the byte array to analyze for encoding detection
     * @return a {@link DetectionResult} object containing the detection outcome
     */
    DetectionResult detect(byte[] data);
}
