package io.github.barhan44.impl;

import io.github.barhan44.model.DetectionResult;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class UTF8EncodingDetectorTest {

    private final UTF8EncodingDetector detector = new UTF8EncodingDetector();

    @Test
    void testDetectValidUTF8() {
        byte[] data = "Test data...".getBytes(StandardCharsets.UTF_8);
        DetectionResult result = detector.detect(data);

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
        assertTrue(result.getConfidence() > 60);
    }

    @Test
    public void testDetectInvalidUTF8() {
        byte[] invalidData = { (byte) 0xFF, (byte) 0xFE, 0x41 };
        DetectionResult result = detector.detect(invalidData);

        assertNull(result);
    }
}