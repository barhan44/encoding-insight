package io.github.barhan44.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for file operations, specifically for reading file contents as byte arrays.
 * <p>
 * Provides methods to read bytes from files or file paths with a maximum byte limit,
 * which is useful for sampling the beginning of files for tasks such as encoding detection.
 * This class is designed to be thread-safe and stateless.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 * byte[] data = FileUtils.readFileBytes("path/to/file.txt", 8192);
 * </pre>
 * <p>
 * Note: This class does not handle character decoding or encoding conversion,
 * it only provides raw byte data from files.
 */
public class FileUtils {
    private FileUtils() {
    }

    /**
     * Reads up to maxBytes bytes from the file specified by the path string.
     *
     * @param path     the string path of the file to read
     * @param maxBytes the maximum number of bytes to read from the file
     * @return a byte array containing up to maxBytes bytes from the beginning of the file
     * @throws IOException if an I/O error occurs reading the file
     */
    public static byte[] readFileBytes(String path, int maxBytes) throws IOException {
        return readFileBytes(Paths.get(path), maxBytes);
    }

    /**
     * Reads up to maxBytes bytes from the file specified by the Path object.
     *
     * @param path     the Path object representing the file to read
     * @param maxBytes the maximum number of bytes to read from the file
     * @return a byte array containing up to maxBytes bytes from the beginning of the file
     * @throws IOException if an I/O error occurs reading the file
     */
    public static byte[] readFileBytes(Path path, int maxBytes) throws IOException {
        byte[] allBytes = Files.readAllBytes(path);
        if (allBytes.length <= maxBytes) {
            return allBytes;
        }
        byte[] result = new byte[maxBytes];
        System.arraycopy(allBytes, 0, result, 0, maxBytes);
        return result;
    }
}
