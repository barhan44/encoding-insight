package io.github.barhan44.encoding.insight.core.api;

import io.github.barhan44.encoding.insight.core.detector.EncodingDetector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class EncodingInsightTest {

    @TempDir
    private File tempDirectory;

    @Test
    void testUTF8_success() throws IOException {
        File testFile = createTestFile("Testing UTF-8", StandardCharsets.UTF_8);
        EncodingDetector detector = EncodingInsight.getDefaultDetector();
        DetectionResult result = detector.detect(testFile);
        assertTrue(result.isSuccess());
        assertEquals(StandardCharsets.UTF_8, result.getCharset());
    }

    @Test
    void testWindows1251_failed() throws IOException {
        File testFile = createTestFile("Тестируем Windows-1251", Charset.forName("Windows-1251"));
        EncodingDetector detector = EncodingInsight.getDefaultDetector();
        DetectionResult result = detector.detect(testFile);
        assertTrue(result.isSuccess());
        assertEquals(Charset.forName("Windows-1251"), result.getCharset());
    }

    @Test
    void testEmptyInput() {
        EncodingDetector detector = EncodingInsight.getDefaultDetector();
        assertThrows(RuntimeException.class, () -> detector.detect(new File(tempDirectory, "empty_file.txt")));
    }

    @Test
    void testNullInput() throws IOException {
        EncodingDetector detector = EncodingInsight.getDefaultDetector();
        assertNull(detector.detect((File) null));
    }

    private File createTestFile(String content, Charset charset) throws IOException {
        File file = new File(tempDirectory, "test.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(charset));
        }
        return file;
    }
}