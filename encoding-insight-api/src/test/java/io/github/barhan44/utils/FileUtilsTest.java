package io.github.barhan44.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileUtilsTest {
    @Test
    void testReadFileBytes() throws IOException {
        Path testfile = Files.createTempFile("testfile", ".txt");
        String content = "Test data...";
        Files.write(testfile, content.getBytes());

        byte[] data = FileUtils.readFileBytes(testfile, 100);
        assertNotNull(data);
        assertEquals(content.length(), data.length);

        Files.delete(testfile);
    }
}