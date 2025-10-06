package io.github.barhan44.impl;

import io.github.barhan44.base.AbstractEncodingDetector;
import io.github.barhan44.model.DetectionResult;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

/**
 * Detector for UTF-8 encoding.
 * <p>
 * Uses multiple strategies including BOM detection, sequence validation,
 * statistical analysis of ASCII vs multibyte characters,
 * and CharsetDecoder validation.
 * </p>
 */
public class UTF8EncodingDetector extends AbstractEncodingDetector {

    private static final byte[] UTF8_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
    private static final int MIN_CONFIDENCE_THRESHOLD = 60;

    public UTF8EncodingDetector() {
        super("UTF-8 Detector", new String[] { "UTF-8" });
    }

    @Override
    public boolean canDetect(byte[] data) {
        return data != null && data.length > 0;
    }

    @Override
    protected DetectionResult performDetection(byte[] data) {

        int confidence = 0;

        if (hasBom(data)) {
            return new DetectionResult("UTF-8", 100, getName());
        }

        ValidationResult validationResult = validateUTF8Sequences(data);
        confidence += validationResult.confidence;

        confidence += performStatisticalAnalysis(data);
        confidence += validateWithDecoder(data);

        if (confidence >= MIN_CONFIDENCE_THRESHOLD) {
            return new DetectionResult("UTF-8", Math.min(confidence, 100), getName());
        }

        return null;
    }

    private boolean hasBom(byte[] data) {
        if (data.length < UTF8_BOM.length) {
            return false;
        }

        for (int i = 0; i < UTF8_BOM.length; i++) {
            if (data[i] != UTF8_BOM[i]) {
                return false;
            }
        }
        return true;
    }

    private ValidationResult validateUTF8Sequences(byte[] data) {
        int validSequences = 0;
        int totalSequences = 0;
        int i = 0;

        while (i < data.length) {
            totalSequences++;

            byte b = data[i];

            if ((b & 0x80) == 0) {
                validSequences++;
                i++;
                continue;
            }

            int sequenceLength = getUTF8SequenceLength(b);

            if (sequenceLength == 0 || i + sequenceLength > data.length) {
                i++;
                continue;
            }

            boolean validSequence = true;

            for (int j = 0; j < sequenceLength; j++) {
                if ((data[i + j] & 0xC0) != 0x80) {
                    validSequence = false;
                    break;
                }
            }

            if (validSequence) {
                validSequences++;
            }

            i += sequenceLength;
        }

        int confidence = totalSequences > 0 ? (validSequences * 40) / totalSequences : 0;
        return new ValidationResult(confidence, validSequences, totalSequences);
    }


    private int performStatisticalAnalysis(byte[] data) {
        int multiByte = 0;
        int ascii = 0;

        for (byte b : data) {
            if ((b & 0x80) == 0) {
                ascii++;
            } else {
                multiByte++;
            }
        }

        if (ascii > data.length * 0.7) {
            return 20;
        }

        if (ascii > 0 && multiByte > 0) {
            return 15;
        }

        return 0;
    }

    private int validateWithDecoder(byte[] data) {
        try {
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT);

            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            CharBuffer charBuffer = CharBuffer.allocate(data.length * 2);

            decoder.decode(byteBuffer, charBuffer, true);
            decoder.flush(charBuffer);

            return 30;
        } catch (Exception e) {
            return 0;
        }
    }

    private int getUTF8SequenceLength(byte firstByte) {
        if ((firstByte & 0x80) == 0) return 1;        // 0xxxxxxx
        if ((firstByte & 0xE0) == 0xC0) return 2;     // 110xxxxx
        if ((firstByte & 0xF0) == 0xE0) return 3;     // 1110xxxx
        if ((firstByte & 0xF8) == 0xF0) return 4;     // 11110xxx
        return 0; // invalid sequence
    }

    private static class ValidationResult {
        final int confidence;
        final int validSequences;
        final int totalSequences;

        public ValidationResult(int confidence, int validSequences, int totalSequences) {
            this.confidence = confidence;
            this.validSequences = validSequences;
            this.totalSequences = totalSequences;
        }
    }
}
