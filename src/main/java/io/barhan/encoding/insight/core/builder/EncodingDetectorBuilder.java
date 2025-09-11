package io.barhan.encoding.insight.core.builder;

import io.barhan.encoding.insight.configuration.Configuration;
import io.barhan.encoding.insight.core.detector.AbstractEncodingDetector;
import io.barhan.encoding.insight.core.detector.EncodingDetector;
import io.barhan.encoding.insight.core.optimization.OptimizationOptions;
import io.barhan.encoding.insight.core.strategy.DetectionStrategy;

public interface EncodingDetectorBuilder {
    EncodingDetectorBuilder withConfiguration(Configuration configuration);

    EncodingDetectorBuilder withStrategy(DetectionStrategy strategy);

    EncodingDetectorBuilder withOptimizations(OptimizationOptions optimizations);

    EncodingDetectorBuilder withDetectorType(Class<? extends AbstractEncodingDetector> detectorType);

    EncodingDetector build();
}
