package io.github.barhan44.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BinaryDataHeuristicTest {

    private static final Charset CP1251 = Charset.forName("Windows-1251");

    @Test
    void nullAndEmptyDataAreNotBinary() {
        assertFalse(BinaryDataHeuristic.isBinary(null));
        assertFalse(BinaryDataHeuristic.isBinary(new byte[0]));
    }

    @Test
    void nulByteMarksDataAsBinary() {
        assertTrue(BinaryDataHeuristic.isBinary(new byte[] { 0x41, 0x00, 0x42 }));
    }

    @Test
    void highControlByteRatioMarksDataAsBinary() {
        byte[] data = new byte[20];
        Arrays.fill(data, (byte) 'A');
        data[0] = 0x01;
        data[1] = 0x02;
        data[2] = 0x03;

        assertTrue(BinaryDataHeuristic.isBinary(data));
    }

    @Test
    void lowControlByteRatioIsStillText() {
        byte[] data = new byte[100];
        Arrays.fill(data, (byte) 'A');
        data[0] = 0x01;

        assertFalse(BinaryDataHeuristic.isBinary(data));
    }

    @Test
    void commonWhitespaceIsNotControlData() {
        assertFalse(BinaryDataHeuristic.isBinary(
                new byte[] { 'A', 0x09, 'B', 0x0A, 'C', 0x0C, 'D', 0x0D }));
    }

    @Test
    void cp1251PunctuationIsNotMistakenForControlCharacters() {
        byte[] data = "Привет — мир «тест»".getBytes(CP1251);

        assertFalse(BinaryDataHeuristic.isBinary(data));
    }
}
