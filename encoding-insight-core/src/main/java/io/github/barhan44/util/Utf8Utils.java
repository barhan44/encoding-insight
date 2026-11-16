package io.github.barhan44.util;

import java.util.Arrays;

/**
 * Utility methods for working with UTF-8 byte buffers.
 * This class is stateless and thread-safe.
 */
public final class Utf8Utils {

    private static final int MAX_SEQUENCE_LENGTH = 4;

    private Utf8Utils() {
    }

    /**
     * Removes an incomplete UTF-8 sequence from the end of the buffer.
     * <p>
     * When a byte array is a prefix of a larger stream, the read boundary may split a
     * multibyte UTF-8 character. Passing such a prefix to an encoding detector would
     * make a valid file look malformed. This method detects a trailing lead byte whose
     * continuation bytes are missing and returns a copy without that incomplete
     * sequence. Complete sequences, ASCII text and data that does not end with a
     * recognizable UTF-8 lead byte are returned unchanged.
     * </p>
     *
     * @param data the bytes to inspect; may be {@code null}
     * @return the original array when no incomplete sequence is present, otherwise a
     * copy truncated before the incomplete sequence
     */
    public static byte[] trimIncompleteTrailingSequence(byte[] data) {
        if (data == null || data.length == 0) {
            return data;
        }

        int scanLimit = Math.max(0, data.length - MAX_SEQUENCE_LENGTH);
        for (int i = data.length - 1; i >= scanLimit; i--) {
            int value = data[i] & 0xFF;
            if ((value & 0xC0) == 0x80) {
                // A continuation byte; keep looking for its leading byte.
                continue;
            }

            int expectedLength = expectedSequenceLength(value);
            if (expectedLength > 1 && i + expectedLength > data.length) {
                return Arrays.copyOf(data, i);
            }
            return data;
        }

        return data;
    }

    private static int expectedSequenceLength(int firstByte) {
        if (firstByte >= 0xC2 && firstByte <= 0xDF) return 2;
        if (firstByte >= 0xE0 && firstByte <= 0xEF) return 3;
        if (firstByte >= 0xF0 && firstByte <= 0xF4) return 4;
        return 0;
    }
}
