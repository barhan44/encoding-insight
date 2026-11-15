package io.github.barhan44;

import io.github.barhan44.model.DetectionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end edge-case tests for the api module: empty files, ASCII, UTF-8 with
 * and without BOM, Windows-1251 with different Cyrillic shares, broken bytes and
 * the 8192-byte sampling limit. Temporary files are managed by {@link TempDir}.
 */
class EncodingDetectionIntegrationEdgeCaseTest {

    private static final Charset CP1251 = Charset.forName("Windows-1251");
    private static final byte[] UTF8_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

    private static final String REAL_RUSSIAN =
            "Тестовая строка для кодировки Windows-1251";
    private static final String ALL_CYRILLIC_RUSSIAN =
            "Съешь ещё этих мягких французских булок да выпей же чаю!";

    @TempDir
    Path tempDir;

    private EncodingDetectionService service;

    @BeforeEach
    void setUp() {
        service = new EncodingInsight().getDetectionService();
    }

    @Test
    void emptyFileReturnsNull() throws IOException {
        Path file = write("empty.txt", new byte[0]);

        assertNull(service.detectFile(file));
        assertNull(service.detectFile(file.toString()));
    }

    @Test
    void asciiFileIsDetectedAsUtf8() throws IOException {
        Path file = write("ascii.txt", "plain ascii text".getBytes(StandardCharsets.US_ASCII));

        DetectionResult result = service.detectFile(file);

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
        assertTrue(result.getConfidence() >= 60);
    }

    @Test
    void detectFileByStringPathWorks() throws IOException {
        Path file = write("ascii-string-path.txt",
                "plain ascii text".getBytes(StandardCharsets.US_ASCII));

        DetectionResult result = service.detectFile(file.toString());

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
    }

    @Test
    void utf8FileWithBomIsDetectedWithFullConfidence() throws IOException {
        byte[] data = concat(UTF8_BOM, "Привет мир".getBytes(StandardCharsets.UTF_8));
        Path file = write("utf8-bom.txt", data);

        DetectionResult result = service.detectFile(file);

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
        assertEquals(100, result.getConfidence());
    }

    @Test
    void allCyrillicCp1251FileIsDetected() throws IOException {
        Path file = write("cp1251-all-cyrillic.txt", ALL_CYRILLIC_RUSSIAN.getBytes(CP1251));

        DetectionResult result = service.detectFile(file);

        assertNotNull(result);
        assertEquals("Windows-1251", result.getEncoding());
        assertTrue(result.getConfidence() >= 50);
    }

    /**
     * Characterizes the documented 8192-byte sampling limit: only the file prefix is
     * analyzed, so a Cyrillic tail beyond the sample is invisible. This test documents
     * current behavior rather than desired behavior.
     */
    @Test
    void fileLargerThanSampleSizeIsDetectedFromPrefixOnly() throws IOException {
        byte[] asciiPrefix = new byte[8192];
        Arrays.fill(asciiPrefix, (byte) 'A');
        Path file = write("large-prefix-ascii.txt",
                concat(asciiPrefix, ALL_CYRILLIC_RUSSIAN.getBytes(CP1251)));

        DetectionResult result = service.detectFile(file);

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding(),
                "only the first 8192 ASCII bytes are analyzed (documented limitation)");
    }

    @Test
    void utf8FileWithoutBomIsDetected() throws IOException {
        Path file = write("utf8-no-bom.txt",
                "Привет мир, это тестовая строка".getBytes(StandardCharsets.UTF_8));

        DetectionResult result = service.detectFile(file);

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
    }

    @Test
    void brokenUtf8FileIsRejected() throws IOException {
        Path file = write("broken-utf8.bin", new byte[] {
                0x41, 0x42, 0x43, (byte) 0xFF, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49
        });

        assertNull(service.detectFile(file));
    }

    @Test
    void cp1251FileWithLatinIsDetectedAsWindows1251() throws IOException {
        Path file = write("cp1251-with-latin.txt", REAL_RUSSIAN.getBytes(CP1251));

        DetectionResult result = service.detectFile(file);

        assertNotNull(result);
        assertEquals("Windows-1251", result.getEncoding());
    }

    @Test
    void cp1251FileWithLowCyrillicShareIsNotReportedAsUtf8() throws IOException {
        String text = repeat("абвгд", 4) + repeat("abcdefghijklmnopqrstuvwxyz", 3) + "ab";
        Path file = write("cp1251-low-cyrillic.txt", text.getBytes(CP1251));

        DetectionResult result = service.detectFile(file);

        assertTrue(result == null || !"UTF-8".equals(result.getEncoding()));
    }

    @Test
    void cp1251FileWithUndefinedByteIsRejected() throws IOException {
        byte[] valid = REAL_RUSSIAN.getBytes(CP1251);
        byte[] withUndefinedByte = Arrays.copyOf(valid, valid.length + 1);
        withUndefinedByte[valid.length] = (byte) 0x98;
        Path file = write("cp1251-undefined-byte.txt", withUndefinedByte);

        assertNull(service.detectFile(file));
    }

    private Path write(String name, byte[] content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.write(file, content);
        return file;
    }

    private static String repeat(String value, int count) {
        StringBuilder result = new StringBuilder(value.length() * count);
        for (int i = 0; i < count; i++) {
            result.append(value);
        }
        return result.toString();
    }

    private static byte[] concat(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
