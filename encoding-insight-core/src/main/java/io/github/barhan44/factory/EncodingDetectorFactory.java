package io.github.barhan44.factory;

import io.github.barhan44.api.EncodingDetector;
import io.github.barhan44.impl.UTF8EncodingDetector;
import io.github.barhan44.impl.Win1251EncodingDetector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Factory class responsible for creating encoding detector instances.
 * <p>
 * Implements the singleton pattern to ensure a single factory instance.
 * Supports registration of detector classes by name and creation of detectors.
 * </p>
 * <p>
 * Uses the registry pattern to manage available detector classes dynamically.
 */
public class EncodingDetectorFactory {
    private static final EncodingDetectorFactory INSTANCE = new EncodingDetectorFactory();

    // Thread-safe map of detector name to detector class
    private final Map<String, Class<? extends EncodingDetector>> detectorsClasses = new HashMap<>();

    // Private constructor for singleton
    private EncodingDetectorFactory() {
        registerDefaultDetectors();
    }

    // Get singleton instance
    public static EncodingDetectorFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Creates an instance of the detector by name.
     *
     * @param name Detector name
     * @return New instance of the requested detector
     * @throws IllegalArgumentException if the detector name is unrecognized
     */
    public EncodingDetector createDetector(String name) {
        Class<? extends EncodingDetector> clazz = detectorsClasses.get(name);
        if (clazz == null) {
            throw new IllegalArgumentException("Unknown detector: " + name);
        }

        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create detector: " + name, e);
        }
    }

    /**
     * Registers a new detector class with a unique name.
     *
     * @param name          Unique name for detector
     * @param detectorClass Class reference implementing EncodingDetector
     */
    public void registerDetector(String name, Class<? extends EncodingDetector> detectorClass) {
        detectorsClasses.put(name, detectorClass);
    }


    /**
     * Retrieves a list of all registered detector names.
     *
     * @return Set of detector names
     */
    public Set<String> getAvailableDetectors() {
        return new HashSet<>(detectorsClasses.keySet());
    }

    /**
     * Registers default detectors, such as UTF-8 and Windows-1251.
     */
    private void registerDefaultDetectors() {
        registerDetector("UTF-8", UTF8EncodingDetector.class);
        registerDetector("Windows-1251", Win1251EncodingDetector.class);
    }
}
