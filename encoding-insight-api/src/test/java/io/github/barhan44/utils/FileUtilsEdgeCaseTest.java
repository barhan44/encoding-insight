package io.github.barhan44.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Test
    void readStreamBytesRespectsLimit() throws IOException {
        InputStream input = new ByteArrayInputStream("abcdef".getBytes(StandardCharsets.US_ASCII));

        byte[] result = FileUtils.readStreamBytes(input, 3);

        assertArrayEquals("abc".getBytes(StandardCharsets.US_ASCII), result);
    }

    @Test
    void readStreamBytesDoesNotCloseCallerStream() throws IOException {
        boolean[] closed = { false };
        InputStream input = new ByteArrayInputStream("abcdef".getBytes(StandardCharsets.US_ASCII)) {
            @Override
            public void close() throws IOException {
                closed[0] = true;
                super.close();
            }
        };

        FileUtils.readStreamBytes(input, 3);

        assertFalse(closed[0], "the caller-owned stream must not be closed");
    }

    @Test
    void readStreamBytesRejectsNegativeLimit() {
        assertThrows(IllegalArgumentException.class,
                () -> FileUtils.readStreamBytes(new ByteArrayInputStream(new byte[0]), -1));
    }

    private Path write(String name, byte[] content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.write(file, content);
        return file;
    }
}
