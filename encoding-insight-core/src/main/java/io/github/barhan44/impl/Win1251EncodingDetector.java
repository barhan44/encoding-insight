package io.github.barhan44.impl;

import io.github.barhan44.base.AbstractEncodingDetector;
import io.github.barhan44.model.DetectionResult;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

/**
 * Detector for Windows-1251 encoding.
 * <p>
 * Uses statistical analysis of Cyrillic content, frequencies,
 * common bigrams and trigrams, and charset decoding validation.
 * Excludes false positives like UTF-8 BOM.
 * </p>
 */
public class Win1251EncodingDetector extends AbstractEncodingDetector {
    public Win1251EncodingDetector() {
        super("Windows-1251 Detector", new String[] { "Windows-1251", "CP1251" });
    }

    @Override
    public boolean canDetect(byte[] data) {
        return data != null && data.length > 10;
    }

    private static final int MIN_CONFIDENCE_THRESHOLD = 50;

    // Russian letter frequency percentages for statistical analysis
    private static final double[] CYRILLIC_FREQUENCIES = {
            8.01, 1.59, 4.54, 1.70, 8.45, 0.04, 0.87, 1.66, 7.35, 1.21, // А-Й
            3.49, 4.40, 3.21, 6.70, 10.97, 2.81, 4.73, 5.47, 6.26, 2.62, // К-Т
            2.62, 0.10, 0.10, 0.03, 2.00, 0.36, 2.01, 1.85, 0.17, 2.00, // У-Я
            0.04, 0.04, 0.03 // Ъ, Ы, Ь
    };

    private static final String[] COMMON_BIGRAMS = {
            "ст", "но", "то", "на", "ен", "ра", "во", "ко", "ро", "ер",
            "не", "ре", "ни", "ов", "ор", "ел", "ос", "ти", "ля", "ал"
    };

    private static final String[] COMMON_TRIGRAMS = {
            "ста", "сто", "ени", "что", "про", "при", "все", "так", "как", "это"
    };

    @Override
    protected DetectionResult performDetection(byte[] data) {
        if (hasUTF8BOM(data)) {
            return null;
        }

        int confidence = 0;

        if (!canDecodeAsCP1251(data)) {
            return null;
        }
        confidence += 20;

        String text = decodeAsCP1251(data);
        if (text == null || text.isEmpty()) {
            return null;
        }
        confidence += analyzeCyrillicContent(text);

        confidence += analyzeCharacterFrequencies(text);

        confidence += analyzeBigrams(text);
        confidence += analyzeTrigrams(text);

        if (confidence >= MIN_CONFIDENCE_THRESHOLD) {
            return new DetectionResult("Windows-1251", Math.min(confidence, 100), getName());
        }

        return null;
    }

    private boolean hasUTF8BOM(byte[] data) {
        if (data.length < 3) return false;
        return data[0] == (byte) 0xEF && data[1] == (byte) 0xBB && data[2] == (byte) 0xBF;
    }

    private boolean canDecodeAsCP1251(byte[] data) {
        try {
            Charset cp1251 = Charset.forName("CP1251");
            CharsetDecoder decoder = cp1251.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT);

            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            CharBuffer charBuffer = CharBuffer.allocate(data.length);

            decoder.decode(byteBuffer, charBuffer, true);
            decoder.flush(charBuffer);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String decodeAsCP1251(byte[] data) {
        try {
            return new String(data, "CP1251");
        } catch (Exception e) {
            return null;
        }
    }

    private int analyzeCyrillicContent(String text) {
        int cyrillicCount = 0;
        int totalChars = 0;

        for (char c : text.toCharArray()) {
            if (Character.getNumericValue(c) != -1 || Character.isLetter(c)) {
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

    private int analyzeCharacterFrequencies(String text) {
        int[] frequencies = new int[33]; // А-Я
        int totalCyrillic = 0;

        for (char c : text.toLowerCase().toCharArray()) {
            int index = getCyrillicIndex(c);
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
        if (c == 'ё') return 32;
        if (c == 'Ё') return 32;
        return -1;
    }

    private int analyzeBigrams(String text) {
        String lowerText = text.toLowerCase();
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

    private int analyzeTrigrams(String text) {
        String lowerText = text.toLowerCase();
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
