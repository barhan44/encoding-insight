package io.github.barhan44.encoding.insight.core.builder;

import io.github.barhan44.encoding.insight.configuration.Configuration;
import io.github.barhan44.encoding.insight.core.detector.AbstractEncodingDetector;
import io.github.barhan44.encoding.insight.core.detector.EncodingDetector;
import io.github.barhan44.encoding.insight.core.optimization.OptimizationOptions;
import io.github.barhan44.encoding.insight.core.strategy.DetectionStrategy;

public interface EncodingDetectorBuilder {
    EncodingDetectorBuilder withConfiguration(Configuration configuration);

    EncodingDetectorBuilder withStrategy(DetectionStrategy strategy);

    EncodingDetectorBuilder withOptimizations(OptimizationOptions optimizations);

    EncodingDetectorBuilder withDetectorType(Class<? extends AbstractEncodingDetector> detectorType);

    EncodingDetector build();
}
