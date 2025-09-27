package io.github.barhan44.encoding.insight.core.api;

import io.github.barhan44.encoding.insight.configuration.Configuration;
import io.github.barhan44.encoding.insight.core.detector.EncodingDetector;
import io.github.barhan44.encoding.insight.core.detector.EncodingDetectorFactory;

public final class EncodingInsight {
    private static final EncodingDetector DEFAULT_DETECTOR = EncodingDetectorFactory.createDefaultDetector();

    private EncodingInsight() {
    }

    public static EncodingDetector getDefaultDetector() {
        return DEFAULT_DETECTOR;
    }

    public static EncodingDetector getDefaultDetector(Configuration configuration) {
        return EncodingDetectorFactory.createDefaultDetector(configuration);
    }
}
