package io.github.barhan44.composite;

import io.github.barhan44.api.EncodingDetector;
import io.github.barhan44.model.DetectionResult;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Composite detector that aggregates multiple encoding detectors.
 * <p>
 * Uses the strategy pattern to delegate detection to multiple detectors
 * and selects the result with the highest confidence.
 * </p>
 * <p>
 * Supports dynamic management of detectors (add, remove, clear).
 * Thread-safe implementation allows concurrent modifications.
 */
public class CompositeEncodingDetector implements EncodingDetector {

    private final List<EncodingDetector> detectors = new CopyOnWriteArrayList<>();
    private final String name;

    /**
     * Constructs a composite detector with the specified name.
     *
     * @param name human-readable name of the composite detector
     */
    public CompositeEncodingDetector(String name) {
        this.name = name;
    }

    /**
     * Constructs a default composite detector with a generic name.
     */
    public CompositeEncodingDetector() {
        this("Composite Detector");
    }

    /**
     * Adds a detector to the composite.
     *
     * @param detector the encoding detector to add
     */
    public void addDetector(EncodingDetector detector) {
        if (detector != null && detector != this) {
            detectors.add(detector);
        }
    }

    /**
     * Removes a detector from the composite.
     *
     * @param detector the encoding detector to remove
     */
    public void removeDetector(EncodingDetector detector) {
        detectors.remove(detector);
    }

    /**
     * Clears all detectors from the composite.
     */
    public void clearDetectors() {
        detectors.clear();
    }

    /**
     * Gets a copy of the list of contained detectors.
     *
     * @return list of detectors
     */
    public List<EncodingDetector> getDetectors() {
        return new ArrayList<>(detectors);
    }

    /**
     * Detects the encoding of the provided data by delegating to child detectors.
     * Returns the detection result with the highest confidence.
     *
     * @param data the data to analyze
     * @return the best detection result or null if no detection was successful
     */
    @Override
    public DetectionResult detect(byte[] data) {
        if (!canDetect(data)) {
            return null;
        }

        List<DetectionResult> results = process(data);

        return results.stream()
                .max(Comparator.comparingInt(DetectionResult::getConfidence))
                .orElse(null);
    }

    /**
     * Detects all encoding results from all child detectors.
     *
     * @param data the data to analyze
     * @return list of all detection results ordered by confidence descending
     */
    public List<DetectionResult> detectAll(byte[] data) {
        if (!canDetect(data)) {
            return Collections.emptyList();
        }

        List<DetectionResult> results = process(data);

        results.sort((dr1, dr2) -> Integer.compare(dr2.getConfidence(), dr1.getConfidence()));

        return results;
    }

    private List<DetectionResult> process(byte[] data) {
        List<DetectionResult> results = new ArrayList<>();

        for (EncodingDetector detector : detectors) {
            try {
                DetectionResult result = detector.detect(data);
                if (result != null) {
                    results.add(result);
                }
            } catch (Exception e) {
                System.err.printf("Detector %s failed: %s%n", detector.getName(), e.getMessage());
            }
        }

        return results;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean canDetect(byte[] data) {
        return !detectors.isEmpty() && data != null && data.length > 0;
    }

    @Override
    public String[] getSupportedEncodings() {
        Set<String> allEncodings = new HashSet<>();
        for (EncodingDetector detector : detectors) {
            allEncodings.addAll(Arrays.asList(detector.getSupportedEncodings()));
        }

        return allEncodings.toArray(new String[0]);
    }
}
