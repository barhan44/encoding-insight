package io.github.barhan44.encoding.insight.core.builder.impl;

import io.github.barhan44.encoding.insight.configuration.Configuration;
import io.github.barhan44.encoding.insight.configuration.DefaultConfiguration;
import io.github.barhan44.encoding.insight.core.builder.EncodingDetectorBuilder;
import io.github.barhan44.encoding.insight.core.detector.AbstractEncodingDetector;
import io.github.barhan44.encoding.insight.core.detector.EncodingDetector;
import io.github.barhan44.encoding.insight.core.detector.impl.UTF8EncodingDetector;
import io.github.barhan44.encoding.insight.core.optimization.OptimizationOptions;
import io.github.barhan44.encoding.insight.core.optimization.impl.DefaultOptimizationOptions;
import io.github.barhan44.encoding.insight.core.strategy.DetectionStrategy;
import io.github.barhan44.encoding.insight.core.strategy.impl.UTF8DetectionStrategy;

public class DefaultEncodingDetectorBuilder implements EncodingDetectorBuilder {

    private Configuration configuration = new DefaultConfiguration();
    private DetectionStrategy strategy = new UTF8DetectionStrategy();
    private OptimizationOptions optimizations = new DefaultOptimizationOptions();
    private Class<? extends AbstractEncodingDetector> detectorType = UTF8EncodingDetector.class;

    @Override
    public EncodingDetectorBuilder withConfiguration(Configuration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        this.configuration = configuration;
        return this;
    }

    @Override
    public EncodingDetectorBuilder withStrategy(DetectionStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        this.strategy = strategy;
        return this;
    }

    @Override
    public EncodingDetectorBuilder withOptimizations(OptimizationOptions optimizations) {
        if (optimizations == null) {
            throw new IllegalArgumentException("Options cannot be null");
        }
        this.optimizations = optimizations;
        return this;
    }

    @Override
    public EncodingDetectorBuilder withDetectorType(Class<? extends AbstractEncodingDetector> detectorType) {
        if (detectorType == null) {
            throw new IllegalArgumentException("DetectorType cannot be null");
        }
        this.detectorType = detectorType;
        return this;
    }

    @Override
    public EncodingDetector build() {
        return AbstractEncodingDetector.createDetector(configuration, strategy, optimizations, detectorType);
    }
}
