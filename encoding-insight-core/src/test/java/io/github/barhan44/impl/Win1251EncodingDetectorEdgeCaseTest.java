package io.github.barhan44.impl;

import io.github.barhan44.model.DetectionResult;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Edge-case tests for {@link Win1251EncodingDetector}, including empty/short input,
 * the 10-byte lower bound, different Cyrillic shares, the frequency heuristic and
 * undefined CP1251 bytes.
 */
class Win1251EncodingDetectorEdgeCaseTest {

    private static final Charset CP1251 = Charset.forName("Windows-1251");
    private static final byte[] UTF8_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

    private static final String REAL_RUSSIAN =
            "Тестовая строка для кодировки Windows-1251";

    private static final String ALL_CYRILLIC_RUSSIAN =
            "Съешь ещё этих мягких французских булок да выпей же чаю!";

    // Natural Russian prose (165 Cyrillic letters) whose letter distribution is close
    // to the reference table, so the chi-square check contributes its maximum bonus.
    private static final String LONG_RUSSIAN_PROSE =
            "В начале июля, в чрезвычайно жаркое время, под вечер, один молодой человек "
                    + "вышел из своей каморки, которую нанимал от жильцов в С-м переулке, "
                    + "на улицу и медленно, как бы в нерешимости, отправился к К-ну мосту.";

    // 100 Cyrillic letters with no common bigrams/trigrams from the detector's dictionaries.
    private static final String CYRILLIC_100 = repeat("абвгд", 20);
    private static final String LATIN_30 = "abcdefghijklmnopqrstuvwxyzabcd";
    private static final String LATIN_40 = "abcdefghijklmnopqrstuvwxyzabcdefghijklmn";
    private static final String LATIN_80 = repeat("abcdefghijklmnopqrstuvwxyz", 3) + "ab";

    private final Win1251EncodingDetector detector = new Win1251EncodingDetector();

    @Test
    void emptyInputReturnsNull() {
        assertNull(detector.detect(new byte[0]));
    }

    @Test
    void nullInputReturnsNull() {
        assertNull(detector.detect(null));
    }

    @Test
    void exactlyTenBytesIsRejectedAsTooShort() {
        byte[] data = cp1251("Привет ми!");

        assertEquals(10, data.length);
        assertNull(detector.detect(data));
    }

    @Test
    void elevenBytesCanBeDetected() {
        byte[] data = cp1251("Привет мир!");

        assertEquals(11, data.length);
        DetectionResult result = detector.detect(data);

        assertNotNull(result);
        assertEquals("Windows-1251", result.getEncoding());
        assertTrue(result.getConfidence() >= 50);
    }

    @Test
    void cyrillicShareAboveSeventyPercentIsDetected() {
        // 100 Cyrillic letters + 30 Latin letters => 76.9% Cyrillic.
        DetectionResult result = detector.detect(cp1251(CYRILLIC_100 + LATIN_30));

        assertNotNull(result);
        assertEquals("Windows-1251", result.getEncoding());
        assertTrue(result.getConfidence() >= 50);
    }

    @Test
    void cyrillicShareAroundSixtyPercentIsNotDetected() {
        // 60 Cyrillic letters + 40 Latin letters => 60% Cyrillic.
        assertNull(detector.detect(cp1251(repeat("абвгд", 12) + LATIN_40)));
    }

    @Test
    void cyrillicShareAroundTwentyPercentIsNotDetected() {
        // 20 Cyrillic letters + 80 Latin letters => 20% Cyrillic.
        assertNull(detector.detect(cp1251(repeat("абвгд", 4) + LATIN_80)));
    }

    @Test
    void asciiOnlyInputIsNotDetected() {
        assertNull(detector.detect("plain ascii text longer than ten bytes"
                .getBytes(StandardCharsets.US_ASCII)));
    }

    @Test
    void utf8BomIsRejected() {
        byte[] data = concat(UTF8_BOM, "plain ascii text".getBytes(StandardCharsets.US_ASCII));

        assertTrue(data.length > 10);
        assertNull(detector.detect(data));
    }

    @Test
    void utf8CyrillicBytesAreNotDetectedAsCp1251() {
        byte[] utf8Bytes = "Привет мир, это тестовая строка".getBytes(StandardCharsets.UTF_8);

        assertNull(detector.detect(utf8Bytes));
    }

    @Test
    void realRussianTextIsDetected() {
        DetectionResult result = detector.detect(cp1251(REAL_RUSSIAN));

        assertNotNull(result);
        assertEquals("Windows-1251", result.getEncoding());
        assertTrue(result.getConfidence() >= 50);
    }

    @Test
    void allCyrillicRussianTextIsDetected() {
        DetectionResult result = detector.detect(cp1251(ALL_CYRILLIC_RUSSIAN));

        assertNotNull(result);
        assertEquals("Windows-1251", result.getEncoding());
        assertTrue(result.getConfidence() >= 50);
    }

    @Test
    void longRussianTextReceivesFrequencyBonus() {
        DetectionResult result = detector.detect(cp1251(LONG_RUSSIAN_PROSE));

        assertNotNull(result);
        assertTrue(result.getConfidence() >= 70,
                "long Russian text should receive the frequency bonus, actual confidence: "
                        + result.getConfidence());
    }

    @Test
    void binaryDataWithNulBytesIsNotDetected() {
        byte[] data = new byte[32];
        Arrays.fill(data, (byte) 'A');
        data[5] = 0x00;
        data[6] = 0x01;

        assertNull(detector.detect(data));
        assertFalse(detector.canDetect(data));
    }

    @Test
    void undefinedCp1251ByteIsRejected() {
        byte[] valid = cp1251(REAL_RUSSIAN);
        byte[] withUndefinedByte = Arrays.copyOf(valid, valid.length + 1);
        withUndefinedByte[valid.length] = (byte) 0x98;

        assertNull(detector.detect(withUndefinedByte));
    }

    private static byte[] cp1251(String value) {
        return value.getBytes(CP1251);
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
