package io.github.barhan44.model;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class DetectionResultTest {

    @Test
    void resolvesCharsetFromEncodingName() {
        DetectionResult utf8 = new DetectionResult("UTF-8", 90, "UTF-8 Detector");
        DetectionResult cp1251 = new DetectionResult("Windows-1251", 50, "Windows-1251 Detector");

        assertEquals("UTF-8", utf8.getEncoding());
        assertSame(StandardCharsets.UTF_8, utf8.getCharset());
        assertEquals(Charset.forName("Windows-1251"), cp1251.getCharset());
    }

    @Test
    void unknownEncodingNameLeavesCharsetNull() {
        DetectionResult result = new DetectionResult("Custom-Encoding", 100, "Custom Detector");

        assertEquals("Custom-Encoding", result.getEncoding());
        assertNull(result.getCharset());
    }

    @Test
    void explicitCharsetIsPreserved() {
        DetectionResult result = new DetectionResult(
                "Windows-1251", 50, "Windows-1251 Detector", Charset.forName("Windows-1251"));

        assertSame(Charset.forName("Windows-1251"), result.getCharset());
    }

    @Test
    void nullEncodingAndCharsetAreAllowed() {
        DetectionResult result = new DetectionResult(null, 0, "Detector", null);

        assertNull(result.getEncoding());
        assertNull(result.getCharset());
    }
}
