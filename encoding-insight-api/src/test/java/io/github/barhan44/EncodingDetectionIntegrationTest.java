package io.github.barhan44;

import io.github.barhan44.model.DetectionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EncodingDetectionIntegrationTest {

    private EncodingDetectionService service;

    EncodingDetectionIntegrationTest() {
    }

    @BeforeEach
    void setUp() {
        EncodingInsight lib = new EncodingInsight();
        service = lib.getDetectionService();
    }

    @Test
    void testUTF8FileDetection() throws IOException {
        Path tempFile = Files.createTempFile("integration_test_utf8", ".txt");
        String content = "This is UTF-8 file.";
        Files.write(tempFile, content.getBytes(StandardCharsets.UTF_8));

        DetectionResult result = service.detectFile(tempFile);

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
        assertTrue(result.getConfidence() > 70);


        Files.delete(tempFile);
    }

    @Test
    void testWin1251FileDetection() throws IOException {
        Path tempFile = Files.createTempFile("integration_test_cp1251", ".txt");
        String content = "Съешь ещё этих мягких французских булок да выпей же чаю!";
        Files.write(tempFile, content.getBytes(Charset.forName("Windows-1251")));

        DetectionResult result = service.detectFile(tempFile);
        assertNotNull(result);
        assertTrue(Charset.isSupported("Windows-1251"));
        assertEquals("Windows-1251", result.getEncoding());
        assertTrue(result.getConfidence() >= 50);

        Files.delete(tempFile);
    }

    @Test
    void testDetectAllReturnsMultipleResults() {
        byte[] bytes = "Test data".getBytes(StandardCharsets.UTF_8);
        List<DetectionResult> detectionResults = service.detectAll(bytes);

        assertFalse(detectionResults.isEmpty());
        assertTrue(detectionResults.stream().anyMatch(r -> "UTF-8".equals(r.getEncoding())));
        assertFalse(detectionResults.stream().anyMatch(r -> "Windows-1251".equals(r.getEncoding())));
    }
}
