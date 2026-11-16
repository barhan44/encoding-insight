package io.github.barhan44.impl;

import io.github.barhan44.model.DetectionResult;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Edge-case tests for {@link UTF8EncodingDetector}: empty input, ASCII, BOM
 * handling, broken and truncated sequences, random binary data and cross-encoding
 * false positives.
 */
class UTF8EncodingDetectorEdgeCaseTest {

    private static final byte[] UTF8_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

    private final UTF8EncodingDetector detector = new UTF8EncodingDetector();

    @Test
    void detectsAsciiOnlyInput() {
        DetectionResult result = detector.detect("plain ascii text".getBytes(StandardCharsets.US_ASCII));

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
        assertTrue(result.getConfidence() >= 60);
    }

    @Test
    void emptyInputReturnsNull() {
        assertNull(detector.detect(new byte[0]));
        assertFalse(detector.canDetect(new byte[0]));
    }

    @Test
    void nullInputReturnsNull() {
        assertNull(detector.detect(null));
        assertFalse(detector.canDetect(null));
    }

    @Test
    void detectsUtf8BomWithFullConfidence() {
        byte[] data = concat(UTF8_BOM, "Привет мир".getBytes(StandardCharsets.UTF_8));

        DetectionResult result = detector.detect(data);

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
        assertEquals(100, result.getConfidence());
    }

    @Test
    void detectsBomOnlyInput() {
        DetectionResult result = detector.detect(UTF8_BOM.clone());

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
        assertEquals(100, result.getConfidence());
    }

    /**
     * Characterizes the absence of a minimum input length: a single ASCII byte is
     * reported as UTF-8. This is documented behavior, not a failing bug.
     */
    @Test
    void detectsSingleByteAsciiInput() {
        DetectionResult result = detector.detect(new byte[] { 0x41 });

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
    }

    @Test
    void detectsValidCyrillicUtf8WithoutBom() {
        byte[] data = "Привет мир, это тестовая строка".getBytes(StandardCharsets.UTF_8);

        DetectionResult result = detector.detect(data);

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
        assertTrue(result.getConfidence() >= 60);
    }

    @Test
    void rejectsInvalidByteSequence() {
        byte[] data = {
                0x41, 0x42, 0x43, (byte) 0xFF, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49
        };

        assertNull(detector.detect(data));
    }

    @Test
    void rejectsTruncatedSequenceAtEnd() {
        byte[] data = {
                0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, (byte) 0xD0
        };

        assertNull(detector.detect(data));
    }

    @Test
    void randomBinaryDataIsNotDetectedAsUtf8() {
        byte[] data = new byte[1024];
        new Random(42).nextBytes(data);

        assertNull(detector.detect(data));
    }

    @Test
    void bomWithTrailingGarbageIsNotPerfectlyConfident() {
        byte[] data = concat(UTF8_BOM, new byte[] { (byte) 0xFF, (byte) 0xFE, 0x41 });

        DetectionResult result = detector.detect(data);

        assertTrue(result == null || result.getConfidence() < 100);
    }

    @Test
    void binaryDataWithNulBytesIsNotDetected() {
        byte[] data = {
                0x41, 0x42, 0x43, 0x00, 0x01, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A
        };

        assertNull(detector.detect(data));
        assertFalse(detector.canDetect(data));
    }

    @Test
    void cp1251BytesWithLatinAreNotDetectedAsUtf8() {
        byte[] data = "Тестовая строка для кодировки Windows-1251"
                .getBytes(Charset.forName("Windows-1251"));

        assertNull(detector.detect(data));
    }

    private static byte[] concat(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
