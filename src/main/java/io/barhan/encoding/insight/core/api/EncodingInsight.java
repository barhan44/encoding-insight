package io.barhan.encoding.insight.core.api;

import io.barhan.encoding.insight.configuration.Configuration;
import io.barhan.encoding.insight.core.builder.impl.DefaultEncodingDetectorBuilder;
import io.barhan.encoding.insight.core.detector.AbstractEncodingDetector;
import io.barhan.encoding.insight.core.detector.EncodingDetector;
import io.barhan.encoding.insight.core.optimization.OptimizationOptions;
import io.barhan.encoding.insight.core.strategy.DetectionStrategy;

public final class EncodingInsight {
    private static final EncodingDetector DEFAULT_DETECTOR = createDefaultDetector();

    private EncodingInsight() {
    }

    public static EncodingDetector getDefaultDetector() {
        return DEFAULT_DETECTOR;
    }

    public static EncodingDetector getDetector(
            Configuration configuration,
            DetectionStrategy strategy,
            OptimizationOptions options,
            Class<? extends AbstractEncodingDetector> detectorType) {
        return new DefaultEncodingDetectorBuilder()
                .withConfiguration(configuration)
                .withStrategy(strategy)
                .withOptimizations(options)
                .withDetectorType(detectorType)
                .build();
    }

    private static EncodingDetector createDefaultDetector() {
        return new DefaultEncodingDetectorBuilder().build();
    }
}
