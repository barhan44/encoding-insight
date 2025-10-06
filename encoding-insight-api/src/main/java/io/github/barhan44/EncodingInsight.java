package io.github.barhan44;

import io.github.barhan44.composite.CompositeEncodingDetector;
import io.github.barhan44.factory.EncodingDetectorFactory;

/**
 * Facade class providing a simple entry point to the encoding detection library.
 * <p>
 * This class initializes the default composite encoding detector consisting of
 * UTF-8 and Windows-1251 detectors and exposes the EncodingDetectionService for use.
 * It abstracts the setup complexities and provides a friendly API for clients.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 * EncodingInsight library = new EncodingInsight();
 * EncodingDetectionService service = library.getDetectionService();
 * DetectionResult result = service.detectFile("example.txt");
 * </pre>
 */
public final class EncodingInsight {

    private final EncodingDetectionService detectionService;

    /**
     * Constructs the EncodingInsight with default detectors (UTF-8 and Windows-1251).
     */
    public EncodingInsight() {
        CompositeEncodingDetector composite = new CompositeEncodingDetector("Default Detector");
        EncodingDetectorFactory factory = EncodingDetectorFactory.getInstance();
        composite.addDetector(factory.createDetector("UTF-8"));
        composite.addDetector(factory.createDetector("Windows-1251"));
        this.detectionService = new EncodingDetectionService(composite);
    }

    /**
     * Returns the encoding detection service instance.
     *
     * @return the EncodingDetectionService for performing detection operations
     */
    public EncodingDetectionService getDetectionService() {
        return detectionService;
    }
}
