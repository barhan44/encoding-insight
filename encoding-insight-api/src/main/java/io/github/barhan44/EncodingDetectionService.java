package io.github.barhan44;

import io.github.barhan44.composite.CompositeEncodingDetector;
import io.github.barhan44.model.DetectionResult;
import io.github.barhan44.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Service class responsible for encoding detection operations.
 * <p>
 * Provides methods to detect the character encoding of byte arrays,
 * files specified by path strings, or Path objects.
 * The service delegates detection tasks to an internal CompositeEncodingDetector
 * which combines multiple encoding detection strategies.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 * EncodingDetectionService service = new EncodingDetectionService(compositeDetector);
 * DetectionResult result = service.detectFile("example.txt");
 * </pre>
 * <p>
 * This class abstracts the complexity of reading file bytes and invoking multiple detectors,
 * providing a streamlined API for encoding detection.
 */
public class EncodingDetectionService {
    private final CompositeEncodingDetector compositeDetector;

    /**
     * Constructs the EncodingDetectionService with a predefined composite detector.
     *
     * @param compositeDetector the composite detector combining multiple encoding detectors
     * @throws IllegalArgumentException if compositeDetector is null
     */
    public EncodingDetectionService(CompositeEncodingDetector compositeDetector) {
        if (compositeDetector == null) {
            throw new IllegalArgumentException("Composite detector cannot be null!");
        }
        this.compositeDetector = compositeDetector;
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
     * Detects the encoding of a file given by its file path string.
     * Reads up to a default number of bytes (e.g., 8192) from the file for detection.
     *
     * @param filePath the path to the file as a string
     * @return the DetectionResult or null if undetectable
     * @throws IOException if there is an error reading the file
     */
    public DetectionResult detectFile(String filePath) throws IOException {
        byte[] data = FileUtils.readFileBytes(filePath, 8192);
        return detect(data);
    }

    /**
     * Detects the encoding of a file represented by a Path object.
     * Reads up to a default number of bytes from the file for detection.
     *
     * @param path the Path object referring to the file
     * @return the DetectionResult or null if undetectable
     * @throws IOException if there is an error reading the file
     */
    public DetectionResult detectFile(Path path) throws IOException {
        byte[] data = FileUtils.readFileBytes(path, 8192);
        return detect(data);
    }
}
