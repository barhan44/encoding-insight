package io.github.barhan44;

import io.github.barhan44.composite.CompositeEncodingDetector;
import io.github.barhan44.model.DetectionResult;
import io.github.barhan44.util.Utf8Utils;
import io.github.barhan44.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

/**
 * Service class responsible for encoding detection operations.
 * <p>
 * Provides methods to detect the character encoding of byte arrays, input streams
 * and files. File and stream detection reads at most the configured sample size
 * (default {@value #DEFAULT_MAX_SAMPLE_BYTES} bytes); the rest of the input is not
 * loaded into memory. An incomplete UTF-8 character at the sample boundary is
 * trimmed before detection, and binary data is rejected by the underlying
 * composite detector.
 * </p>
 * <p>
 * The service delegates detection tasks to an internal
 * {@link CompositeEncodingDetector} which combines multiple encoding detection
 * strategies.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 * EncodingDetectionService service = new EncodingDetectionService(compositeDetector);
 * DetectionResult result = service.detectFile("example.txt");
 * </pre>
 */
public class EncodingDetectionService {

    /**
     * Default maximum number of leading bytes sampled from a file or stream.
     */
    public static final int DEFAULT_MAX_SAMPLE_BYTES = 32768;

    private final CompositeEncodingDetector compositeDetector;
    private final int maxSampleBytes;

    /**
     * Constructs the service with the default sample size.
     *
     * @param compositeDetector the composite detector combining multiple encoding detectors
     * @throws IllegalArgumentException if compositeDetector is null
     */
    public EncodingDetectionService(CompositeEncodingDetector compositeDetector) {
        this(compositeDetector, DEFAULT_MAX_SAMPLE_BYTES);
    }

    /**
     * Constructs the service with a custom sample size.
     *
     * @param compositeDetector the composite detector combining multiple encoding detectors
     * @param maxSampleBytes    maximum number of leading bytes read from a file or stream
     * @throws IllegalArgumentException if compositeDetector is null or maxSampleBytes is not positive
     */
    public EncodingDetectionService(CompositeEncodingDetector compositeDetector, int maxSampleBytes) {
        if (compositeDetector == null) {
            throw new IllegalArgumentException("Composite detector cannot be null!");
        }
        if (maxSampleBytes <= 0) {
            throw new IllegalArgumentException(
                    "maxSampleBytes must be positive: " + maxSampleBytes);
        }
        this.compositeDetector = compositeDetector;
        this.maxSampleBytes = maxSampleBytes;
    }

    /**
     * Returns the configured maximum number of bytes sampled from files and streams.
     *
     * @return maximum sample size in bytes
     */
    public int getMaxSampleBytes() {
        return maxSampleBytes;
    }

    /**
     * Detects the encoding of a given byte array.
     *
     * @param data a byte array representing the data whose encoding is to be detected
     * @return the DetectionResult with encoding, confidence and detector name,
     * or null if encoding could not be determined
     */
    public DetectionResult detect(byte[] data) {
        return compositeDetector.detect(data);
    }

    /**
     * Detects all possible encodings of the given byte array, returning a list
     * of potential matches sorted by confidence.
     *
     * @param data a byte array representing the data to analyze
     * @return a list of DetectionResult sorted in descending order of confidence,
     * or an empty list if no encodings could be detected
     */
    public List<DetectionResult> detectAll(byte[] data) {
        return compositeDetector.detectAll(data);
    }

    /**
     * Detects the encoding of a stream using the configured sample size.
     * <p>
     * At most {@link #getMaxSampleBytes()} bytes are read. The stream is not closed
     * by this method; the caller remains responsible for closing it.
     * </p>
     *
     * @param input the stream to sample
     * @return the DetectionResult or null if undetectable
     * @throws IOException if an I/O error occurs reading the stream
     */
    public DetectionResult detect(InputStream input) throws IOException {
        return detect(input, maxSampleBytes);
    }

    /**
     * Detects the encoding of a stream using an explicit maximum sample size.
     * <p>
     * The stream is not closed by this method; the caller remains responsible for
     * closing it.
     * </p>
     *
     * @param input    the stream to sample
     * @param maxBytes maximum number of bytes to read (must be positive)
     * @return the DetectionResult or null if undetectable
     * @throws IOException              if an I/O error occurs reading the stream
     * @throws IllegalArgumentException if maxBytes is not positive
     */
    public DetectionResult detect(InputStream input, int maxBytes) throws IOException {
        validateMaxBytes(maxBytes);
        return detect(readSample(input, maxBytes));
    }

    /**
     * Detects all possible encodings of a stream using the configured sample size.
     * The stream is not closed by this method.
     *
     * @param input the stream to sample
     * @return a list of DetectionResult sorted by confidence, or an empty list
     * @throws IOException if an I/O error occurs reading the stream
     */
    public List<DetectionResult> detectAll(InputStream input) throws IOException {
        return detectAll(input, maxSampleBytes);
    }

    /**
     * Detects all possible encodings of a stream using an explicit maximum sample size.
     * The stream is not closed by this method.
     *
     * @param input    the stream to sample
     * @param maxBytes maximum number of bytes to read (must be positive)
     * @return a list of DetectionResult sorted by confidence, or an empty list
     * @throws IOException              if an I/O error occurs reading the stream
     * @throws IllegalArgumentException if maxBytes is not positive
     */
    public List<DetectionResult> detectAll(InputStream input, int maxBytes) throws IOException {
        validateMaxBytes(maxBytes);
        return detectAll(readSample(input, maxBytes));
    }

    /**
     * Detects the encoding of a file using the configured sample size.
     *
     * @param filePath the path to the file as a string
     * @return the DetectionResult or null if undetectable
     * @throws IOException if there is an error reading the file
     */
    public DetectionResult detectFile(String filePath) throws IOException {
        return detectFile(filePath, maxSampleBytes);
    }

    /**
     * Detects the encoding of a file using an explicit maximum sample size.
     *
     * @param filePath the path to the file as a string
     * @param maxBytes maximum number of bytes to read (must be positive)
     * @return the DetectionResult or null if undetectable
     * @throws IOException              if there is an error reading the file
     * @throws IllegalArgumentException if maxBytes is not positive
     */
    public DetectionResult detectFile(String filePath, int maxBytes) throws IOException {
        validateMaxBytes(maxBytes);
        return detect(Utf8Utils.trimIncompleteTrailingSequence(
                FileUtils.readFileBytes(filePath, maxBytes)));
    }

    /**
     * Detects the encoding of a file using the configured sample size.
     *
     * @param path the Path object referring to the file
     * @return the DetectionResult or null if undetectable
     * @throws IOException if there is an error reading the file
     */
    public DetectionResult detectFile(Path path) throws IOException {
        return detectFile(path, maxSampleBytes);
    }

    /**
     * Detects the encoding of a file using an explicit maximum sample size.
     *
     * @param path     the Path object referring to the file
     * @param maxBytes maximum number of bytes to read (must be positive)
     * @return the DetectionResult or null if undetectable
     * @throws IOException              if there is an error reading the file
     * @throws IllegalArgumentException if maxBytes is not positive
     */
    public DetectionResult detectFile(Path path, int maxBytes) throws IOException {
        validateMaxBytes(maxBytes);
        return detect(readSample(path, maxBytes));
    }

    private byte[] readSample(Path path, int maxBytes) throws IOException {
        return Utf8Utils.trimIncompleteTrailingSequence(
                FileUtils.readFileBytes(path, maxBytes));
    }

    private byte[] readSample(InputStream input, int maxBytes) throws IOException {
        return Utf8Utils.trimIncompleteTrailingSequence(
                FileUtils.readStreamBytes(input, maxBytes));
    }

    private static void validateMaxBytes(int maxBytes) {
        if (maxBytes <= 0) {
            throw new IllegalArgumentException("maxBytes must be positive: " + maxBytes);
        }
    }
}
