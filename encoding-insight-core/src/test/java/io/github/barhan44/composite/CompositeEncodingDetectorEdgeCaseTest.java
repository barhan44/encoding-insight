package io.github.barhan44.composite;

import io.github.barhan44.impl.UTF8EncodingDetector;
import io.github.barhan44.impl.Win1251EncodingDetector;
import io.github.barhan44.model.DetectionResult;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Edge-case tests for {@link CompositeEncodingDetector}: empty input, encoding
 * selection, broken data and concurrent use of a shared composite.
 */
class CompositeEncodingDetectorEdgeCaseTest {

    private static final byte[] UTF8_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

    @Test
    void emptyInputReturnsNullAndEmptyList() {
        CompositeEncodingDetector composite = createDefaultComposite();

        assertNull(composite.detect(new byte[0]));
        assertTrue(composite.detectAll(new byte[0]).isEmpty());
    }

    @Test
    void nullInputReturnsNullAndEmptyList() {
        CompositeEncodingDetector composite = createDefaultComposite();

        assertNull(composite.detect(null));
        assertTrue(composite.detectAll(null).isEmpty());
    }

    @Test
    void compositeWithoutDetectorsReturnsNull() {
        CompositeEncodingDetector composite = new CompositeEncodingDetector();

        assertFalse(composite.canDetect("plain ascii text".getBytes(StandardCharsets.US_ASCII)));
        assertNull(composite.detect("plain ascii text".getBytes(StandardCharsets.US_ASCII)));
    }

    @Test
    void asciiInputIsDetectedAsUtf8() {
        DetectionResult result = createDefaultComposite()
                .detect("plain ascii text".getBytes(StandardCharsets.US_ASCII));

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
    }

    @Test
    void utf8BomInputIsDetectedWithFullConfidence() {
        byte[] data = concat(UTF8_BOM, "Привет мир".getBytes(StandardCharsets.UTF_8));

        DetectionResult result = createDefaultComposite().detect(data);

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
        assertEquals(100, result.getConfidence());
    }

    @Test
    void allCyrillicCp1251InputIsDetectedAsWindows1251() {
        byte[] data = "Съешь ещё этих мягких французских булок да выпей же чаю!"
                .getBytes(Charset.forName("Windows-1251"));

        DetectionResult result = createDefaultComposite().detect(data);

        assertNotNull(result);
        assertEquals("Windows-1251", result.getEncoding());
    }

    @Test
    void validCyrillicUtf8WithoutBomIsDetected() {
        byte[] data = "Привет мир, это тестовая строка".getBytes(StandardCharsets.UTF_8);

        DetectionResult result = createDefaultComposite().detect(data);

        assertNotNull(result);
        assertEquals("UTF-8", result.getEncoding());
    }

    @Test
    void randomBinaryDataIsNotDetected() {
        byte[] data = new byte[1024];
        new Random(42).nextBytes(data);

        assertNull(createDefaultComposite().detect(data));
    }

    @Test
    void cp1251TextWithLatinIsDetectedAsWindows1251() {
        byte[] data = "Тестовая строка для кодировки Windows-1251"
                .getBytes(Charset.forName("Windows-1251"));

        DetectionResult result = createDefaultComposite().detect(data);

        assertNotNull(result);
        assertEquals("Windows-1251", result.getEncoding());
    }

    @Test
    void binaryDataIsRejected() {
        byte[] data = new byte[64];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) 'A';
        }
        data[10] = 0x00;
        data[11] = 0x01;

        CompositeEncodingDetector composite = createDefaultComposite();

        assertFalse(composite.canDetect(data));
        assertNull(composite.detect(data));
        assertTrue(composite.detectAll(data).isEmpty());
    }

    @Test
    void concurrentDetectionOnSharedCompositeIsSafe() throws Exception {
        CompositeEncodingDetector composite = createDefaultComposite();
        byte[] data = "plain ascii text".getBytes(StandardCharsets.US_ASCII);
        int threads = 8;
        int iterations = 100;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            futures.add(pool.submit(() -> {
                start.await();
                for (int j = 0; j < iterations; j++) {
                    DetectionResult result = composite.detect(data);
                    if (result == null || !"UTF-8".equals(result.getEncoding())) {
                        return false;
                    }
                }
                return true;
            }));
        }

        start.countDown();

        try {
            for (Future<Boolean> future : futures) {
                assertTrue(future.get(10, TimeUnit.SECONDS));
            }
        } finally {
            pool.shutdownNow();
        }
    }

    private static CompositeEncodingDetector createDefaultComposite() {
        CompositeEncodingDetector composite = new CompositeEncodingDetector("Test Composite");
        composite.addDetector(new UTF8EncodingDetector());
        composite.addDetector(new Win1251EncodingDetector());
        return composite;
    }

    private static byte[] concat(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
