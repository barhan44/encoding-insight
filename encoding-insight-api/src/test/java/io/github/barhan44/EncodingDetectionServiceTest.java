package io.github.barhan44;

import io.github.barhan44.composite.CompositeEncodingDetector;
import io.github.barhan44.factory.EncodingDetectorFactory;
import io.github.barhan44.model.DetectionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EncodingDetectionServiceTest {

    private EncodingDetectionService service;

    @BeforeEach
    void setUp() {
        CompositeEncodingDetector composite = new CompositeEncodingDetector();
        EncodingDetectorFactory factory = EncodingDetectorFactory.getInstance();
        composite.addDetector(factory.createDetector("UTF-8"));
        composite.addDetector(factory.createDetector("Windows-1251"));
        this.service = new EncodingDetectionService(composite);
    }

    @Test
    void testDetectFromFile() throws IOException {
        Path tempFile = Files.createTempFile("testUTF8", ".txt");
        Files.write(tempFile, "Test data...".getBytes(StandardCharsets.UTF_8));

        DetectionResult result = service.detectFile(tempFile);
        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());

        Files.delete(tempFile);
    }

    @Test
    void testDetectFromBytes() {
        byte[] data = "Тестовая строка...".getBytes(Charset.forName("Windows-1251"));
        DetectionResult result = service.detect(data);

        assertNotNull(result);
        assertEquals("Windows-1251", result.getEncoding());
    }
}