package io.github.barhan44.encoding.insight.core.detector;

import io.github.barhan44.encoding.insight.core.api.DetectionResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents an encoding detector that can analyze files and input streams
 * to determine their character encoding
 */
public interface EncodingDetector {
    /**
     * Detects the character encoding of a file
     *
     * @param file the file to analyze
     * @return the detection result containing the detected charset and confidence level
     * @throws IOException              if an I/O error occurs while reading the file
     * @throws IllegalArgumentException if the file is null or does not exist
     */
    DetectionResult detect(File file) throws IOException;

    /**
     * Detects the character encoding of an input stream
     *
     * @param inputStream the input stream to analyze
     * @return the detection result containing the detected charset and confidence level
     * @throws IOException              if an I/O error occurs while reading the stream
     * @throws IllegalArgumentException if the input stream is null
     */
    DetectionResult detect(InputStream inputStream) throws IOException;
}
