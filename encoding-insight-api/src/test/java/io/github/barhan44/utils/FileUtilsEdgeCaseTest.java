package io.github.barhan44.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Edge-case tests for {@link FileUtils}. The implementation reads at most
 * {@code maxBytes} bytes from disk and always closes the stream.
 */
class FileUtilsEdgeCaseTest {

    @TempDir
    Path tempDir;

    @Test
    void emptyFileReturnsEmptyArray() throws IOException {
        Path file = write("empty.txt", new byte[0]);

        byte[] result = FileUtils.readFileBytes(file, 100);

        assertEquals(0, result.length);
    }

    @Test
    void readsAtMostMaxBytes() throws IOException {
        Path file = write("ten-bytes.txt", "abcdefghij".getBytes(StandardCharsets.US_ASCII));

        byte[] result = FileUtils.readFileBytes(file, 4);

        assertArrayEquals("abcd".getBytes(StandardCharsets.US_ASCII), result);
    }

    @Test
    void returnsWholeFileWhenSmallerThanMaxBytes() throws IOException {
        byte[] content = "abc".getBytes(StandardCharsets.US_ASCII);
        Path file = write("three-bytes.txt", content);

        byte[] result = FileUtils.readFileBytes(file, 100);

        assertArrayEquals(content, result);
    }

    @Test
    void zeroMaxBytesReturnsEmptyArray() throws IOException {
        Path file = write("non-empty.txt", "abcdef".getBytes(StandardCharsets.US_ASCII));

        byte[] result = FileUtils.readFileBytes(file, 0);

        assertEquals(0, result.length);
    }

    @Test
    void negativeMaxBytesIsRejected() throws IOException {
        Path file = write("non-empty-negative.txt", "abcdef".getBytes(StandardCharsets.US_ASCII));

        assertThrows(IllegalArgumentException.class, () -> FileUtils.readFileBytes(file, -1));
    }

    private Path write(String name, byte[] content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.write(file, content);
        return file;
    }
}
