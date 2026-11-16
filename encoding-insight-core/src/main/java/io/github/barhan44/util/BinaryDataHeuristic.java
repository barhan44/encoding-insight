package io.github.barhan44.util;

/**
 * Heuristic that distinguishes binary data from text before encoding detection.
 * <p>
 * Ordinary text in UTF-8 or Windows-1251 does not contain NUL bytes, and C0 control
 * characters other than common whitespace are rare. The heuristic therefore treats
 * data containing a NUL byte as binary, and data whose C0 control-byte ratio exceeds
 * {@value #MAX_CONTROL_RATIO} as binary.
 * </p>
 * <p>
 * The heuristic is deliberately conservative so that Windows-1251 punctuation
 * (for example, the em dash {@code 0x97} or guillemets {@code 0xAB}/{@code 0xBB})
 * is not mistaken for control characters.
 * </p>
 * This class is stateless and thread-safe.
 */
public final class BinaryDataHeuristic {

    private static final double MAX_CONTROL_RATIO = 0.10;
    private static final int TAB = 0x09;
    private static final int LINE_FEED = 0x0A;
    private static final int FORM_FEED = 0x0C;
    private static final int CARRIAGE_RETURN = 0x0D;
    private static final int DELETE = 0x7F;

    private BinaryDataHeuristic() {
    }

    /**
     * Checks whether the provided data looks like binary content.
     *
     * @param data the bytes to inspect; may be {@code null}
     * @return {@code true} if a NUL byte is present or the ratio of C0 control bytes
     * (excluding tab, line feed, form feed and carriage return) exceeds the threshold
     */
    public static boolean isBinary(byte[] data) {
        if (data == null || data.length == 0) {
            return false;
        }

        int controlCount = 0;
        for (byte b : data) {
            int value = b & 0xFF;
            if (value == 0x00) {
                return true;
            }
            if (isControl(value)) {
                controlCount++;
            }
        }

        return (double) controlCount / data.length > MAX_CONTROL_RATIO;
    }

    private static boolean isControl(int value) {
        if (value == TAB || value == LINE_FEED || value == FORM_FEED
                || value == CARRIAGE_RETURN) {
            return false;
        }
        return value < 0x20 || value == DELETE;
    }
}
