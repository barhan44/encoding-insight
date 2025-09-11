package io.barhan.encoding.insight.core.detector.impl;

import io.barhan.encoding.insight.configuration.Configuration;
import io.barhan.encoding.insight.core.api.DetectionResult;
import io.barhan.encoding.insight.core.detector.AbstractEncodingDetector;
import io.barhan.encoding.insight.core.optimization.OptimizationOptions;
import io.barhan.encoding.insight.core.strategy.DetectionStrategy;

public class UTF8EncodingDetector extends AbstractEncodingDetector {

    public UTF8EncodingDetector(Configuration configuration, DetectionStrategy strategy, OptimizationOptions options) {
        super(configuration, strategy, options);
    }

    @Override
    protected DetectionResult performDetection(byte[] data) {
        return strategy.detect(data);
    }

}
