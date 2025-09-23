package io.github.barhan44.encoding.insight.core.detector.impl;

import io.github.barhan44.encoding.insight.configuration.Configuration;
import io.github.barhan44.encoding.insight.core.api.DetectionResult;
import io.github.barhan44.encoding.insight.core.detector.AbstractEncodingDetector;
import io.github.barhan44.encoding.insight.core.optimization.OptimizationOptions;
import io.github.barhan44.encoding.insight.core.strategy.DetectionStrategy;

public class UTF8EncodingDetector extends AbstractEncodingDetector {

    public UTF8EncodingDetector(Configuration configuration, DetectionStrategy strategy, OptimizationOptions options) {
        super(configuration, strategy, options);
    }

    @Override
    protected DetectionResult performDetection(byte[] data) {
        return strategy.detect(data);
    }

}
