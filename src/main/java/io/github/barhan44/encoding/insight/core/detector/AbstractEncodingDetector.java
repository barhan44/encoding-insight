package io.github.barhan44.encoding.insight.core.detector;

import io.github.barhan44.encoding.insight.configuration.Configuration;
import io.github.barhan44.encoding.insight.core.api.DetectionResult;
import io.github.barhan44.encoding.insight.core.strategy.DetectionStrategy;
import io.github.barhan44.encoding.insight.core.strategy.impl.DefaultDetectionStrategy;

import java.io.*;

public abstract class AbstractEncodingDetector implements EncodingDetector {
    protected final Configuration configuration;
    protected final DetectionStrategy strategy;

    protected AbstractEncodingDetector(Configuration configuration, DetectionStrategy strategy) {
        this.configuration = configuration;
        this.strategy = strategy;
    }

    protected AbstractEncodingDetector(Configuration configuration) {
        this(configuration, new DefaultDetectionStrategy());
    }

    protected DetectionResult performDetection(byte[] data) {
        return strategy.detect(data);
    }

    @Override
    public DetectionResult detect(File file) {
        if (file == null) return null;
        try (InputStream input = new FileInputStream(file)) {
            byte[] data = readInputStream(input);
            return performDetection(data);
        } catch (IOException e) {
            throw new RuntimeException("File reading error", e);
        }
    }

    @Override
    public DetectionResult detect(InputStream inputStream) {
        byte[] data = readInputStream(inputStream);
        return performDetection(data);
    }

    private byte[] readInputStream(InputStream inputStream) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[configuration.getBufferSize()];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("InputStream reading error", e);
        }
    }
}
