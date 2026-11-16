package io.github.barhan44.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class Utf8UtilsTest {

    @Test
    void nullDataReturnsNull() {
        assertNull(Utf8Utils.trimIncompleteTrailingSequence(null));
    }

    @Test
    void completeAsciiDataIsReturnedUnchanged() {
        byte[] data = "hello".getBytes(StandardCharsets.US_ASCII);

        assertSame(data, Utf8Utils.trimIncompleteTrailingSequence(data));
    }

    @Test
    void completeMultibyteSequenceIsKept() {
        byte[] data = { 0x41, (byte) 0xD0, (byte) 0x9F };

        assertArrayEquals(data, Utf8Utils.trimIncompleteTrailingSequence(data));
    }

    @Test
    void incompleteTwoByteSequenceIsTrimmed() {
        byte[] data = { 0x41, (byte) 0xD0 };

        assertArrayEquals(new byte[] { 0x41 }, Utf8Utils.trimIncompleteTrailingSequence(data));
    }

    @Test
    void incompleteThreeByteSequenceIsTrimmed() {
        byte[] data = { 0x41, (byte) 0xE2, (byte) 0x82 };

        assertArrayEquals(new byte[] { 0x41 }, Utf8Utils.trimIncompleteTrailingSequence(data));
    }

    @Test
    void incompleteFourByteSequenceIsTrimmed() {
        byte[] data = { 0x41, (byte) 0xF0, (byte) 0x9F, (byte) 0x98 };

        assertArrayEquals(new byte[] { 0x41 }, Utf8Utils.trimIncompleteTrailingSequence(data));
    }

    @Test
    void invalidTrailingByteIsKept() {
        byte[] data = { 0x41, (byte) 0xFF };

        assertArrayEquals(data, Utf8Utils.trimIncompleteTrailingSequence(data));
    }

    @Test
    void orphanContinuationByteIsKept() {
        byte[] data = { 0x41, (byte) 0x80 };

        assertArrayEquals(data, Utf8Utils.trimIncompleteTrailingSequence(data));
    }
}
