package io.github.barhan44.impl;

import io.github.barhan44.base.AbstractEncodingDetector;
import io.github.barhan44.model.DetectionResult;
import io.github.barhan44.util.BinaryDataHeuristic;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Locale;

/**
 * Detector for Windows-1251 encoding.
 * <p>
 * The detector strictly decodes the input once and combines the following signals:
 * <ul>
 *   <li>decoding success (Windows-1251 is single-byte, so this also rejects bytes
 *       that are undefined in the encoding, such as {@code 0x98});</li>
 *   <li>the share of Cyrillic letters in the decoded text;</li>
 *   <li>a chi-square comparison of letter frequencies against Russian language
 *       frequencies;</li>
 *   <li>the presence of common Russian bigrams and trigrams.</li>
 * </ul>
 * A UTF-8 BOM immediately disqualifies the input. Short inputs (10 bytes or less)
 * are rejected by {@link #canDetect(byte[])}. The detector has no mutable state and
 * is safe for concurrent use.
 */
public class Win1251EncodingDetector extends AbstractEncodingDetector {

    private static final Charset CP1251 = Charset.forName("Windows-1251");
    private static final int MIN_CONFIDENCE_THRESHOLD = 50;
    private static final int MIN_DATA_LENGTH = 10;
    private static final int DECODING_CONFIDENCE = 20;

    // Russian letter frequencies in percent, in the order а..я with ё at index 32.
    // The values sum to approximately 100.
    private static final double[] CYRILLIC_FREQUENCIES = {
            8.01, 1.59, 4.54, 1.70, 2.98, 8.45, 0.94, 1.65, 7.35, 1.21, // а-й
            3.49, 4.40, 3.21, 6.70, 10.97, 2.81, 4.73, 5.47, 6.26, 2.62, // к-т
            2.62, 0.26, 0.97, 0.48, 1.44, 0.73, 0.36, 0.04, 1.90, 1.74, // у-ь
            0.32, 0.64, 2.01, 0.04 // э, ю, я, ё
    };

    private static final String[] COMMON_BIGRAMS = {
            "ст", "но", "то", "на", "ен", "ра", "во", "ко", "ро", "ер",
            "не", "ре", "ни", "ов", "ор", "ел", "ос", "ти", "ля", "ал"
    };

    private static final String[] COMMON_TRIGRAMS = {
            "ста", "сто", "ени", "что", "про", "при", "все", "так", "как", "это"
    };

    public Win1251EncodingDetector() {
        super("Windows-1251 Detector", new String[] { "Windows-1251", "CP1251" });
    }

    @Override
    public boolean canDetect(byte[] data) {
        return data != null
                && data.length > MIN_DATA_LENGTH
                && !BinaryDataHeuristic.isBinary(data);
    }

    @Override
    protected DetectionResult performDetection(byte[] data) {
        if (hasUTF8BOM(data)) {
            return null;
        }

        String text;
        try {
            text = decodeStrictly(data);
        } catch (CharacterCodingException e) {
            return null;
        }

        if (text.isEmpty()) {
            return null;
        }

        String lowerText = text.toLowerCase(Locale.ROOT);
        int confidence = DECODING_CONFIDENCE;

        confidence += analyzeCyrillicContent(text);
        confidence += analyzeCharacterFrequencies(lowerText);
        confidence += analyzeBigrams(lowerText);
        confidence += analyzeTrigrams(lowerText);

        if (confidence >= MIN_CONFIDENCE_THRESHOLD) {
            return new DetectionResult("Windows-1251", Math.min(confidence, 100), getName(), CP1251);
        }

        return null;
    }

    private boolean hasUTF8BOM(byte[] data) {
        if (data.length < 3) return false;
        return data[0] == (byte) 0xEF && data[1] == (byte) 0xBB && data[2] == (byte) 0xBF;
    }

    /**
     * Decodes strictly, rejecting malformed and unmappable bytes. The convenience
     * {@link CharsetDecoder#decode(ByteBuffer)} method throws on such input, whereas
     * the three-argument variant only returns a {@code CoderResult}.
     */
    private String decodeStrictly(byte[] data) throws CharacterCodingException {
        CharsetDecoder decoder = CP1251.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);

        return decoder.decode(ByteBuffer.wrap(data)).toString();
    }

    private int analyzeCyrillicContent(String text) {
        int cyrillicCount = 0;
        int totalChars = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetter(c) || Character.isDigit(c)) {
                totalChars++;
                if (isCyrillic(c)) {
                    cyrillicCount++;
                }
            }
        }

        if (totalChars == 0) return 0;

        double cyrillicRatio = (double) cyrillicCount / totalChars;

        if (cyrillicRatio > 0.7) return 30;
        if (cyrillicRatio > 0.5) return 20;
        if (cyrillicRatio > 0.3) return 10;

        return 0;
    }

    private boolean isCyrillic(char c) {
        return (c >= 'А' && c <= 'я') || c == 'Ё' || c == 'ё';
    }

    private int analyzeCharacterFrequencies(String lowerText) {
        int[] frequencies = new int[CYRILLIC_FREQUENCIES.length]; // а-я и ё
        int totalCyrillic = 0;

        for (int i = 0; i < lowerText.length(); i++) {
            int index = getCyrillicIndex(lowerText.charAt(i));
            if (index >= 0) {
                frequencies[index]++;
                totalCyrillic++;
            }
        }

        if (totalCyrillic < 50) return 0;

        double chiSquare = 0;
        for (int i = 0; i < frequencies.length; i++) {
            double expected = (CYRILLIC_FREQUENCIES[i] / 100.0) * totalCyrillic;
            double observed = frequencies[i];
            if (expected > 0) {
                chiSquare += Math.pow(observed - expected, 2) / expected;
            }
        }

        if (chiSquare < 50) return 15;
        if (chiSquare < 100) return 10;
        if (chiSquare < 200) return 5;

        return 0;
    }

    private int getCyrillicIndex(char c) {
        if (c >= 'а' && c <= 'я') return c - 'а';
        if (c >= 'А' && c <= 'Я') return c - 'А';
        if (c == 'ё' || c == 'Ё') return 32;
        return -1;
    }

    private int analyzeBigrams(String lowerText) {
        int foundBigrams = 0;

        for (String bigram : COMMON_BIGRAMS) {
            if (lowerText.contains(bigram)) {
                foundBigrams++;
            }
        }

        double ratio = (double) foundBigrams / COMMON_BIGRAMS.length;
        if (ratio > 0.7) return 15;
        if (ratio > 0.5) return 10;
        if (ratio > 0.3) return 5;

        return 0;
    }

    private int analyzeTrigrams(String lowerText) {
        int foundTrigrams = 0;

        for (String trigram : COMMON_TRIGRAMS) {
            if (lowerText.contains(trigram)) {
                foundTrigrams++;
            }
        }

        double ratio = (double) foundTrigrams / COMMON_TRIGRAMS.length;
        if (ratio > 0.6) return 10;
        if (ratio > 0.4) return 7;
        if (ratio > 0.2) return 3;

        return 0;
    }
}
