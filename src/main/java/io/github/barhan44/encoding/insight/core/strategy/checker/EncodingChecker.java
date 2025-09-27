package io.github.barhan44.encoding.insight.core.strategy.checker;

import java.nio.charset.Charset;

/**
 * Interface for encoding checkers that perform specific detection logic.
 * Each implementation is responsible for checking a particular encoding type
 * and determining if the provided data matches that encoding.
 */
public interface EncodingChecker {
    /**
     * Checks if the provided byte array matched the encoding handled by this checker
     *
     * @param data byte array to analyze
     * @return detected Charset if the data matches the encoding, or null if it doesn't
     */
    Charset check(byte[] data);
}
