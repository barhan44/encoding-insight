package io.github.barhan44.encoding.insight.core.detector;

import io.github.barhan44.encoding.insight.configuration.Configuration;
import io.github.barhan44.encoding.insight.configuration.DefaultConfiguration;
import io.github.barhan44.encoding.insight.core.detector.impl.DefaultEncodingDetector;

public class EncodingDetectorFactory {
    public static EncodingDetector createDefaultDetector() {
        Configuration defaultConfiguration = new DefaultConfiguration();
        return new DefaultEncodingDetector(defaultConfiguration);
    }

    public static EncodingDetector createDefaultDetector(Configuration configuration) {
        return new DefaultEncodingDetector(configuration);
    }
}
