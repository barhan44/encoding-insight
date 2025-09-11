package io.barhan.encoding.insight.core.optimization.impl;

import io.barhan.encoding.insight.core.optimization.OptimizationOptions;

public class DefaultOptimizationOptions implements OptimizationOptions {

    private boolean enableCaching;

    public OptimizationOptions withCaching(boolean enable) {
        this.enableCaching = enable;
        return this;
    }

    @Override
    public boolean isCachingEnabled() {
        return enableCaching;
    }
}
