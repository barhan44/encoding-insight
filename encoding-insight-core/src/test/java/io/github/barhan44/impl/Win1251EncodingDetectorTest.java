package io.github.barhan44.impl;

import io.github.barhan44.model.DetectionResult;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;

class Win1251EncodingDetectorTest {

    private final Win1251EncodingDetector detector = new Win1251EncodingDetector();

    @Test
    void testDetectValidCp1251() throws UnsupportedEncodingException {
        byte[] data = "Тестовая строка для кодировки Windows-1251".getBytes("Windows-1251");
        DetectionResult result = detector.detect(data);

        assertNotNull(result);
        assertEquals("Windows-1251", result.getEncoding());
        assertTrue(result.getConfidence() >= 50);
    }

    @Test
    void testRejectUTF8BOM() {
        byte[] data = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF, 0x41 };
        DetectionResult result = detector.detect(data);
        assertNull(result);
    }
}