package io.barhan.encoding.insight.core.strategy.impl;

import io.barhan.encoding.insight.core.api.DetectionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class UTF8DetectionStrategyTest {
    public static final byte[] UTF8_WITH_BOM = new byte[] {
            (byte) 0xEF, (byte) 0xBB, (byte) 0xBF,
            (byte) 0x48, (byte) 0x65, (byte) 0x6C, (byte) 0x6C, (byte) 0x6F // Hello
    };

    public static final byte[] UTF8_WITHOUT_BOM = new byte[] {
            (byte) 0xC3, (byte) 0xA9, (byte) 0x66, (byte) 0x69, (byte) 0x73, (byte) 0x68 // éfish
    };

    public static final byte[] INVALID_UTF8 = new byte[] {
            (byte) 0xF0, (byte) 0x90, (byte) 0x80, (byte) 0x80, (byte) 0x80 // Недопустимая последовательность
    };

    public static final byte[] ASCII_ONLY = new byte[] {
            (byte) 0x48, (byte) 0x65, (byte) 0x6C, (byte) 0x6C, (byte) 0x6F // Hello
    };

    public static final byte[] PARTIAL_SEQUENCE = new byte[] {
            (byte) 0xC3 // Неполная последовательность
    };

    private UTF8DetectionStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new UTF8DetectionStrategy();
    }

    @Test
    void testValidUTF8WithBOM() {
        DetectionResult result = strategy.detect(UTF8_WITH_BOM);
        assertTrue(result.isSuccess());
        assertEquals(StandardCharsets.UTF_8, result.getCharset());
    }

    @Test
    void testValidUTF8WithoutBOM() {
        DetectionResult result = strategy.detect(UTF8_WITHOUT_BOM);
        assertTrue(result.isSuccess());
        assertEquals(StandardCharsets.UTF_8, result.getCharset());
    }

    @Test
    void testInvalidUTF8() {
        DetectionResult result = strategy.detect(INVALID_UTF8);
        assertFalse(result.isSuccess());
        assertNull(result.getCharset());
    }

    @Test
    void testASCIIData() {
        DetectionResult result = strategy.detect(ASCII_ONLY);
        assertTrue(result.isSuccess());
        assertEquals(StandardCharsets.UTF_8, result.getCharset());
    }

    @Test
    void testPartialSequence() {
        DetectionResult result = strategy.detect(PARTIAL_SEQUENCE);
        assertFalse(result.isSuccess());
        assertNull(result.getCharset());
    }

    @Test
    void testEmptyData() {
        DetectionResult result = strategy.detect(new byte[0]);
        assertFalse(result.isSuccess());
        assertNull(result.getCharset());
    }

    @Test
    void testNullData() {
        DetectionResult result = strategy.detect(null);
        assertFalse(result.isSuccess());
        assertNull(result.getCharset());
    }
}