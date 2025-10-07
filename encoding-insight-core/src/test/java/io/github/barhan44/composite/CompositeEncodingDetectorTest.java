package io.github.barhan44.composite;

import io.github.barhan44.impl.UTF8EncodingDetector;
import io.github.barhan44.impl.Win1251EncodingDetector;
import io.github.barhan44.model.DetectionResult;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompositeEncodingDetectorTest {

    @Test
    void testDetectReturnsBestResult() {
        CompositeEncodingDetector composite = new CompositeEncodingDetector();
        composite.addDetector(new UTF8EncodingDetector());
        composite.addDetector(new Win1251EncodingDetector());

        byte[] utf8Data = "Test data...".getBytes(StandardCharsets.UTF_8);
        DetectionResult result = composite.detect(utf8Data);

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
    }

    @Test
    void testDetectAllReturnsAllResults() {
        CompositeEncodingDetector composite = new CompositeEncodingDetector();
        composite.addDetector(new UTF8EncodingDetector());
        composite.addDetector(new Win1251EncodingDetector());

        byte[] data = "Test data...".getBytes(StandardCharsets.UTF_8);
        List<DetectionResult> detectionResults = composite.detectAll(data);

        assertFalse(detectionResults.isEmpty());
        assertTrue(detectionResults.stream().anyMatch(r -> r.getEncoding().equals("UTF-8")));
    }
}