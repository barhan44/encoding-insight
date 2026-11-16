package io.github.barhan44.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Utility class for file operations, specifically for reading file contents as byte arrays.
 * <p>
 * Provides methods to read at most a given number of bytes from a file, which is useful
 * for sampling the beginning of files for tasks such as encoding detection. Only the
 * requested number of bytes is read from disk; the whole file is never loaded into
 * memory when a smaller limit is given. The stream is always closed.
 * </p>
 * <p>
 * This class is stateless and thread-safe.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 * byte[] data = FileUtils.readFileBytes("path/to/file.txt", 8192);
 * </pre>
 */
public class FileUtils {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private FileUtils() {
    }

    /**
     * Reads up to {@code maxBytes} bytes from the file specified by the path string.
     *
     * @param path     the string path of the file to read
     * @param maxBytes the maximum number of bytes to read from the file (must be non-negative)
     * @return a byte array containing up to {@code maxBytes} bytes from the beginning of the file
     * @throws IOException              if an I/O error occurs reading the file
     * @throws IllegalArgumentException if {@code maxBytes} is negative
     */
    public static byte[] readFileBytes(String path, int maxBytes) throws IOException {
        Objects.requireNonNull(path, "path");
        return readFileBytes(Paths.get(path), maxBytes);
    }

    /**
     * Reads up to {@code maxBytes} bytes from the file specified by the Path object.
     * <p>
     * Only {@code maxBytes} bytes are read from the stream; the file is not read in full
     * when it is larger than the limit.
     * </p>
     *
     * @param path     the Path object representing the file to read
     * @param maxBytes the maximum number of bytes to read from the file (must be non-negative)
     * @return a byte array containing up to {@code maxBytes} bytes from the beginning of the file
     * @throws IOException              if an I/O error occurs reading the file
     * @throws IllegalArgumentException if {@code maxBytes} is negative
     */
    public static byte[] readFileBytes(Path path, int maxBytes) throws IOException {
        Objects.requireNonNull(path, "path");
        validateMaxBytes(maxBytes);
        if (maxBytes == 0) {
            return new byte[0];
        }

        try (InputStream input = Files.newInputStream(path)) {
            return readBounded(input, maxBytes);
        }
    }

    /**
     * Reads up to {@code maxBytes} bytes from the provided stream.
     * <p>
     * The method reads at most {@code maxBytes} bytes and does <strong>not</strong>
     * close the stream; the caller remains responsible for closing it. This makes the
     * method suitable for reading a bounded sample from a larger stream without
     * consuming or closing the rest of it.
     * </p>
     *
     * @param input    the stream to read from
     * @param maxBytes the maximum number of bytes to read (must be non-negative)
     * @return a byte array containing up to {@code maxBytes} bytes
     * @throws IOException              if an I/O error occurs reading the stream
     * @throws IllegalArgumentException if {@code maxBytes} is negative
     */
    public static byte[] readStreamBytes(InputStream input, int maxBytes) throws IOException {
        Objects.requireNonNull(input, "input");
        validateMaxBytes(maxBytes);
        if (maxBytes == 0) {
            return new byte[0];
        }
        return readBounded(input, maxBytes);
    }

    private static byte[] readBounded(InputStream input, int maxBytes) throws IOException {
        int bufferSize = Math.min(maxBytes, DEFAULT_BUFFER_SIZE);
        ByteArrayOutputStream output = new ByteArrayOutputStream(bufferSize);
        byte[] buffer = new byte[bufferSize];
        int remaining = maxBytes;

        while (remaining > 0) {
            int read = input.read(buffer, 0, Math.min(buffer.length, remaining));
            if (read == -1) {
                break;
            }
            output.write(buffer, 0, read);
            remaining -= read;
        }

        return output.toByteArray();
    }

    private static void validateMaxBytes(int maxBytes) {
        if (maxBytes < 0) {
            throw new IllegalArgumentException("maxBytes must not be negative: " + maxBytes);
        }
    }
}
