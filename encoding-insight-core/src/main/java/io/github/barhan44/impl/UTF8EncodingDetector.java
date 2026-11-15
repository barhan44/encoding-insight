package io.github.barhan44.impl;

import io.github.barhan44.base.AbstractEncodingDetector;
import io.github.barhan44.model.DetectionResult;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

/**
 * Detector for UTF-8 encoding.
 * <p>
 * The detector combines several checks:
 * <ul>
 *   <li>a UTF-8 byte order mark (BOM) yields maximum confidence, but only after the
 *       remaining payload has also been validated as well-formed UTF-8;</li>
 *   <li>without a BOM, confidence is accumulated from UTF-8 sequence validation,
 *       ASCII vs. multibyte statistics and a strict {@link CharsetDecoder} pass;</li>
 *   <li>an input scoring below the internal threshold is reported as undetectable
 *       ({@code null}) instead of being guessed.</li>
 * </ul>
 * The detector has no mutable state and is safe for concurrent use.
 */
public class UTF8EncodingDetector extends AbstractEncodingDetector {

    private static final byte[] UTF8_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
    private static final int MIN_CONFIDENCE_THRESHOLD = 60;
    private static final int BOM_CONFIDENCE = 100;
    private static final int SEQUENCE_MAX_CONFIDENCE = 40;
    private static final int DECODER_CONFIDENCE = 30;

    public UTF8EncodingDetector() {
        super("UTF-8 Detector", new String[] { "UTF-8" });
    }

    @Override
    public boolean canDetect(byte[] data) {
        return data != null && data.length > 0;
    }

    @Override
    protected DetectionResult performDetection(byte[] data) {
        if (hasBom(data)) {
            boolean payloadValid = data.length == UTF8_BOM.length
                    || isValidUtf8(data, UTF8_BOM.length);
            return payloadValid
                    ? new DetectionResult("UTF-8", BOM_CONFIDENCE, getName())
                    : null;
        }

        int confidence = validateUtf8Sequences(data);
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

    /**
     * Counts structurally valid UTF-8 sequences and converts the ratio into points.
     * Continuation bytes are checked starting at index 1: the leading byte itself is
     * not a continuation byte.
     */
    private int validateUtf8Sequences(byte[] data) {
        int validSequences = 0;
        int totalSequences = 0;
        int i = 0;

        while (i < data.length) {
            totalSequences++;

            int firstByte = data[i] & 0xFF;
            if (firstByte < 0x80) {
                validSequences++;
                i++;
                continue;
            }

            int sequenceLength = getUtf8SequenceLength(firstByte);

            if (sequenceLength == 0 || i + sequenceLength > data.length) {
                i++;
                continue;
            }

            boolean validSequence = true;

            for (int j = 1; j < sequenceLength; j++) {
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

        return totalSequences == 0
                ? 0
                : (validSequences * SEQUENCE_MAX_CONFIDENCE) / totalSequences;
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

    /**
     * Validates the whole input with a strict decoder. The three-argument
     * {@link CharsetDecoder#decode(ByteBuffer, java.nio.CharBuffer, boolean)} does not
     * throw on malformed input for {@link CodingErrorAction#REPORT}; the single-argument
     * convenience method does, which makes this check effective.
     */
    private int validateWithDecoder(byte[] data) {
        return isValidUtf8(data, 0) ? DECODER_CONFIDENCE : 0;
    }

    private boolean isValidUtf8(byte[] data, int offset) {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);

        try {
            decoder.decode(ByteBuffer.wrap(data, offset, data.length - offset));
            return true;
        } catch (CharacterCodingException e) {
            return false;
        }
    }

    /**
     * Returns the length of the sequence introduced by the given leading byte, or 0
     * for an invalid/overlong leading byte. Lead bytes C0, C1 (overlong encodings)
     * and F5-FF (outside the Unicode range) are rejected here.
     */
    private int getUtf8SequenceLength(int firstByte) {
        if (firstByte < 0x80) return 1;                    // 0xxxxxxx
        if (firstByte >= 0xC2 && firstByte <= 0xDF) return 2; // 110xxxxx
        if (firstByte >= 0xE0 && firstByte <= 0xEF) return 3; // 1110xxxx
        if (firstByte >= 0xF0 && firstByte <= 0xF4) return 4; // 11110xxx
        return 0;
    }
}
